package com.example.textile.service.impl;

import com.example.textile.dto.request.CreateOrderRequest;
import com.example.textile.dto.request.OrderLineRequest;
import com.example.textile.dto.request.UpdateOrderRequest;
import com.example.textile.dto.response.OrderResponse;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.entity.*;
import com.example.textile.exception.BusinessException;
import com.example.textile.exception.InvalidOperationException;
import com.example.textile.exception.ResourceNotFoundException;
import com.example.textile.mapper.CommandeMapper;
import com.example.textile.repository.ClientRepository;
import com.example.textile.repository.CommandeRepository;
import com.example.textile.repository.ProduitRepository;
import com.example.textile.service.CommandeService;
import com.example.textile.util.OrderNumberGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final CommandeMapper commandeMapper;

    public CommandeServiceImpl(CommandeRepository commandeRepository,
                               ClientRepository clientRepository,
                               ProduitRepository produitRepository,
                               CommandeMapper commandeMapper) {
        this.commandeRepository = commandeRepository;
        this.clientRepository = clientRepository;
        this.produitRepository = produitRepository;
        this.commandeMapper = commandeMapper;
    }

    @Override
    public OrderResponse create(CreateOrderRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> ResourceNotFoundException.of("Client", request.getClientId()));
        if (!client.isActive()) {
            throw new BusinessException("Le client " + client.getName() + " est desactive");
        }
        Commande commande = commandeMapper.toEntity(request, client);
        commande.setNumero(genererNumero());
        for (OrderLineRequest ligneRequest : request.getLignes()) {
            Produit produit = produitRepository.findById(ligneRequest.getProduitId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Produit", ligneRequest.getProduitId()));
            if (!produit.isActive()) {
                throw new BusinessException("Le produit " + produit.getCode() + " est desactive");
            }
            commande.addLigne(commandeMapper.toLigneEntity(ligneRequest, produit));
        }
        Commande saved = commandeRepository.save(commande);
        return commandeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        return commandeMapper.toResponse(findWithCollections(id));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getByNumero(String numero) {
        return commandeRepository.findByNumero(numero)
                .map(commandeMapper::toResponse)
                .orElseThrow(() -> ResourceNotFoundException.of("Commande", numero));
    }

    @Override
    public OrderResponse update(Long id, UpdateOrderRequest request) {
        Commande commande = find(id);
        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new InvalidOperationException("Une commande livree ne peut plus etre modifiee");
        }
        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new InvalidOperationException("Une commande annulee ne peut plus etre modifiee");
        }
        if (request.isVide()) {
            throw new BusinessException("Aucun champ a modifier n'a ete fourni");
        }
        if (request.getClientId() != null && !request.getClientId().equals(commande.getClient().getId())) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Client", request.getClientId()));
            commande.setClient(client);
        }
        commandeMapper.applyUpdate(commande, request);
        if (request.getLignes() != null && !request.getLignes().isEmpty()) {
            if (commande.getStatut() == StatutCommande.EN_PRODUCTION) {
                throw new InvalidOperationException(
                        "Impossible de modifier les lignes : la production a deja demarre");
            }
            remplacerLignes(commande, request.getLignes());
        }
        Commande saved = commandeRepository.save(commande);
        return commandeMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Commande commande = find(id);
        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new InvalidOperationException("Une commande livree ne peut pas etre supprimee");
        }
        if (commande.getStatut() == StatutCommande.EN_PRODUCTION) {
            throw new InvalidOperationException(
                    "Impossible de supprimer une commande en cours de production. Annulez-la plutot.");
        }
        commandeRepository.delete(commande);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> search(String search, StatutCommande statut, PrioriteCommande priorite,
                                              Long clientId, boolean delayedOnly, Pageable pageable) {
        return PageResponse.from(
                commandeRepository.search(statut, priorite, clientId, normalize(search), delayedOnly,
                        LocalDate.now(), pageable),
                c -> commandeMapper.toResponse(chargerCollection(c)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listDelayed() {
        return commandeRepository.findDelayed(LocalDate.now()).stream()
                .map(c -> commandeMapper.toResponse(c))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listActive() {
        return commandeRepository.findActiveOrdered().stream()
                .map(commandeMapper::toResponse)
                .toList();
    }

    // ---------------------------------------------------------------- helpers

    private void remplacerLignes(Commande commande, List<OrderLineRequest> lignes) {
        Set<Long> produitsUniques = new HashSet<>();
        commande.getLignes().clear();
        for (OrderLineRequest ligneRequest : lignes) {
            if (!produitsUniques.add(ligneRequest.getProduitId())) {
                throw new BusinessException("Le produit " + ligneRequest.getProduitId()
                        + " apparait plusieurs fois dans la commande");
            }
            Produit produit = produitRepository.findById(ligneRequest.getProduitId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Produit", ligneRequest.getProduitId()));
            if (!produit.isActive()) {
                throw new BusinessException("Le produit " + produit.getCode() + " est desactive");
            }
            BigDecimal prix = ligneRequest.getPrixUnitaire() != null
                    ? ligneRequest.getPrixUnitaire()
                    : produit.getPrixUnitaire();
            if (prix.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("Le prix unitaire ne peut pas etre negatif");
            }
            commande.addLigne(commandeMapper.toLigneEntity(ligneRequest, produit));
        }
    }

    private String genererNumero() {
        long sequence = commandeRepository.count() + 1;
        String numero = OrderNumberGenerator.next(OrderNumberGenerator.defaultPrefix(), sequence);
        while (commandeRepository.existsByNumero(numero)) {
            sequence++;
            numero = OrderNumberGenerator.next(OrderNumberGenerator.defaultPrefix(), sequence);
        }
        return numero;
    }

    private Commande find(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Commande", id));
    }

    /** Charge les collections lazy dans la session courante (DTO sans lazy). */
    private Commande findWithCollections(Long id) {
        return chargerCollection(find(id));
    }

    private static Commande chargerCollection(Commande commande) {
        commande.getLignes().size();
        commande.getEtapes().size();
        commande.getControleQualite();
        return commande;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}