package com.example.textile.service.impl;

import com.example.textile.dto.request.CompleteStepRequest;
import com.example.textile.dto.request.DeliveryRequest;
import com.example.textile.dto.request.QualityControlRequest;
import com.example.textile.dto.request.StartProductionRequest;
import com.example.textile.dto.response.DeliveryResponse;
import com.example.textile.dto.response.OrderResponse;
import com.example.textile.dto.response.ProductionResponse;
import com.example.textile.entity.*;
import com.example.textile.exception.BusinessException;
import com.example.textile.exception.ConflictException;
import com.example.textile.exception.InvalidOperationException;
import com.example.textile.exception.ResourceNotFoundException;
import com.example.textile.mapper.CommandeMapper;
import com.example.textile.repository.AppUserRepository;
import com.example.textile.repository.CommandeRepository;
import com.example.textile.repository.LivraisonRepository;
import com.example.textile.service.ProductionService;
import com.example.textile.util.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class ProductionServiceImpl implements ProductionService {

    private static final Map<TypeEtape, Integer> DUREE_ETAPE = new LinkedHashMap<>();

    static {
        DUREE_ETAPE.put(TypeEtape.COMMANDE, 1);
        DUREE_ETAPE.put(TypeEtape.COUPE, 2);
        DUREE_ETAPE.put(TypeEtape.COUTURE, 3);
        DUREE_ETAPE.put(TypeEtape.FINITION, 2);
        DUREE_ETAPE.put(TypeEtape.CONTROLE_QUALITE, 1);
        DUREE_ETAPE.put(TypeEtape.LIVRAISON, 1);
    }

    private final CommandeRepository commandeRepository;
    private final LivraisonRepository livraisonRepository;
    private final AppUserRepository appUserRepository;
    private final CommandeMapper commandeMapper;
    private final CurrentUser currentUser;

    public ProductionServiceImpl(CommandeRepository commandeRepository,
                                LivraisonRepository livraisonRepository,
                                AppUserRepository appUserRepository,
                                CommandeMapper commandeMapper,
                                CurrentUser currentUser) {
        this.commandeRepository = commandeRepository;
        this.livraisonRepository = livraisonRepository;
        this.appUserRepository = appUserRepository;
        this.commandeMapper = commandeMapper;
        this.currentUser = currentUser;
    }

    // ------------------------------------------------------------ production

    @Override
    public OrderResponse startProduction(Long commandeId, StartProductionRequest request) {
        Commande commande = find(commandeId);
        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new InvalidOperationException("Une commande livree ne peut pas lancer sa production");
        }
        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new InvalidOperationException("Une commande annulee ne peut pas lancer sa production");
        }
        if (!commande.getEtapes().isEmpty()) {
            throw new ConflictException("La production de la commande " + commande.getNumero()
                    + " a deja ete demarree");
        }
        if (commande.getLignes().isEmpty()) {
            throw new BusinessException("La commande ne contient aucune ligne : production impossible");
        }
        if (commande.getDateLivraisonPrevue().isBefore(LocalDate.now())) {
            throw new BusinessException("La date prevue de livraison est deja depassee : "
                    + "replanifiez la commande avant de lancer la production");
        }

        AppUser responsable = resolveUser(request != null ? request.getResponsableId() : null);
        LocalDate dateLimite = commande.getDateLivraisonPrevue().minusDays(1);

        for (TypeEtape type : TypeEtape.values()) {
            int duree = DUREE_ETAPE.get(type);
            LocalDateTime finPrevue = type == TypeEtape.LIVRAISON
                    ? commande.getDateLivraisonPrevue().atTime(18, 0)
                    : LocalDateTime.now().plusDays(duree);
            if (finPrevue.toLocalDate().isAfter(dateLimite) && type != TypeEtape.LIVRAISON) {
                finPrevue = dateLimite.atTime(18, 0);
            }
            commande.addEtape(new EtapeProduction(commande, type, finPrevue));
        }

        // L'etape COMMANDE est cloturee immediatement : la commande est validee et
        // les quantites commandees constituent la base de production.
        EtapeProduction etapeCommande = commande.getEtape(TypeEtape.COMMANDE);
        etapeCommande.setStatut(StatutEtape.TERMINEE);
        etapeCommande.setDateDebut(LocalDateTime.now());
        etapeCommande.setDateFinReelle(LocalDateTime.now());
        etapeCommande.setResponsable(responsable);
        etapeCommande.setQuantiteProduite(commande.getQuantiteTotale());
        for (LigneCommande ligne : commande.getLignes()) {
            ligne.setQuantiteProduite(ligne.getQuantite());
        }

        // La premiere etape metier (COUPE) devient l'etape courante.
        EtapeProduction premiereEtape = commande.getEtape(TypeEtape.COUPE);
        premiereEtape.setStatut(StatutEtape.EN_COURS);
        premiereEtape.setDateDebut(LocalDateTime.now());
        premiereEtape.setResponsable(responsable);

        commande.setStatut(StatutCommande.EN_PRODUCTION);
        commande.setDateFinProductionPrevue(commande.getDateLivraisonPrevue());

        return commandeMapper.toResponse(commandeRepository.save(commande));
    }

    @Override
    public OrderResponse startStep(Long commandeId, TypeEtape type, String commentaire) {
        Commande commande = find(commandeId);
        if (commande.getStatut() == StatutCommande.LIVREE || commande.getStatut() == StatutCommande.ANNULEE) {
            throw new InvalidOperationException("Cette commande est terminee : aucune etape ne peut demarrer");
        }
        if (commande.getEtape(type) == null) {
            throw new BusinessException("La production n'a pas ete demarree pour la commande "
                    + commande.getNumero());
        }
        EtapeProduction etape = commande.getEtape(type);
        if (etape.getStatut() == StatutEtape.TERMINEE) {
            throw new ConflictException("L'etape " + type.getLibelle() + " est deja terminee");
        }
        if (etape.getStatut() == StatutEtape.EN_COURS) {
            // L'etape est deja demarree ( premiere etape lancee avec la production,
            // ou etape demarree precedemment ) : operation idempotente.
            return commandeMapper.toResponse(commandeRepository.save(commande));
        }

        TypeEtape precedente = type.precedent();
        if (precedente != null) {
            EtapeProduction etapePrecedente = commande.getEtape(precedente);
            if (etapePrecedente == null || !etapePrecedente.isTerminee()) {
                throw new InvalidOperationException("Impossible de demarrer " + type.getLibelle()
                        + " : l'etape " + precedente.getLibelle() + " n'est pas terminee");
            }
            if (type != TypeEtape.CONTROLE_QUALITE && type != TypeEtape.LIVRAISON) {
                verifierQuantitiesDisponibles(commande, type);
            }
        }

        etape.setStatut(StatutEtape.EN_COURS);
        etape.setDateDebut(LocalDateTime.now());
        etape.setResponsable(currentUser.currentOrNull());
        if (commentaire != null && !commentaire.isBlank()) {
            etape.setCommentaire(commentaire);
        }
        if (commande.getStatut() == StatutCommande.PLANIFIEE || commande.getStatut() == StatutCommande.BROUILLON) {
            commande.setStatut(StatutCommande.EN_PRODUCTION);
        }
        return commandeMapper.toResponse(commandeRepository.save(commande));
    }

    @Override
    public OrderResponse completeStep(Long commandeId, TypeEtape type, CompleteStepRequest request) {
        Commande commande = find(commandeId);
        if (commande.getStatut() == StatutCommande.LIVREE || commande.getStatut() == StatutCommande.ANNULEE) {
            throw new InvalidOperationException("Cette commande est terminee : aucune etape ne peut etre cloturee");
        }
        if (type == TypeEtape.CONTROLE_QUALITE) {
            throw new BusinessException("L'etape de controle qualite se cloture via "
                    + "POST /api/orders/{id}/quality-control");
        }
        if (type == TypeEtape.LIVRAISON) {
            throw new BusinessException("L'etape de livraison se cloture via "
                    + "POST /api/orders/{id}/delivery");
        }

        EtapeProduction etape = commande.getEtape(type);
        if (etape == null) {
            throw new BusinessException("La production n'a pas ete demarree pour la commande "
                    + commande.getNumero());
        }
        if (etape.isTerminee()) {
            throw new ConflictException("L'etape " + type.getLibelle() + " est deja terminee");
        }
        if (etape.getStatut() != StatutEtape.EN_COURS) {
            throw new InvalidOperationException("L'etape " + type.getLibelle()
                    + " doit etre demarree avant d'etre cloturee");
        }

        TypeEtape precedente = type.precedent();
        if (precedente != null) {
            EtapeProduction etapePrecedente = commande.getEtape(precedente);
            if (etapePrecedente == null || !etapePrecedente.isTerminee()) {
                throw new InvalidOperationException("Impossible de terminer " + type.getLibelle()
                        + " : l'etape " + precedente.getLibelle() + " n'est pas terminee");
            }
        }

        int quantiteTotale = appliquerQuantites(commande, type, request);
        if (quantiteTotale <= 0) {
            throw new BusinessException("La quantite produite doit etre strictement positive");
        }

        etape.setStatut(StatutEtape.TERMINEE);
        etape.setDateFinReelle(LocalDateTime.now());
        if (etape.getDateDebut() == null) {
            etape.setDateDebut(LocalDateTime.now());
        }
        etape.setQuantiteProduite(quantiteTotale);
        if (request != null && request.getCommentaire() != null && !request.getCommentaire().isBlank()) {
            etape.setCommentaire(request.getCommentaire());
        }

        if (type == TypeEtape.FINITION) {
            commande.setDateFinProductionReelle(LocalDate.now());
            commande.setStatut(StatutCommande.EN_CONTROLE);
        }
        return commandeMapper.toResponse(commandeRepository.save(commande));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductionResponse getProduction(Long commandeId) {
        return commandeMapper.toProductionResponse(commandeRepository.findById(commandeId)
                .orElseThrow(() -> ResourceNotFoundException.of("Commande", commandeId)));
    }

    // ------------------------------------------------------- controle qualite

    @Override
    public OrderResponse qualityControl(Long commandeId, QualityControlRequest request) {
        Commande commande = find(commandeId);
        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new InvalidOperationException("Une commande livree ne peut plus etre controlee");
        }
        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new InvalidOperationException("Une commande annulee ne peut plus etre controlee");
        }
        if (commande.getEtape(TypeEtape.FINITION) == null) {
            throw new BusinessException("Le controle qualite est impossible : la production n'a pas demarre");
        }
        EtapeProduction finition = commande.getEtape(TypeEtape.FINITION);
        if (!finition.isTerminee()) {
            throw new InvalidOperationException("Le controle qualite est impossible : "
                    + "l'etape Finition n'est pas terminee");
        }
        if (commande.getControleQualite() != null) {
            throw new ConflictException("Un controle qualite a deja ete effectue pour la commande "
                    + commande.getNumero());
        }

        AppUser controleur = resolveUser(request.getControleurId());

        ControleQualite controle = new ControleQualite(commande, request.getResultat(), controleur,
                request.getObservations());
        controle.setPointsNonConformites(request.getPointsNonConformites());
        controle.setQuantiteControlee(commande.getQuantiteProduiteTotale());
        commande.setControleQualite(controle);

        EtapeProduction etapeControle = commande.getEtape(TypeEtape.CONTROLE_QUALITE);
        etapeControle.setStatut(StatutEtape.TERMINEE);
        etapeControle.setDateDebut(OptionalDate(etapeControle.getDateDebut()));
        etapeControle.setDateFinReelle(LocalDateTime.now());
        etapeControle.setResponsable(controleur);
        etapeControle.setQuantiteProduite(commande.getQuantiteProduiteTotale());
        String commentaire = request.getResultat() == ResultatControle.CONFORME
                ? "Controle qualite : CONFORME"
                : "Controle qualite : NON CONFORME";
        etapeControle.setCommentaire(commentaire);

        commande.setStatut(StatutCommande.EN_CONTROLE);
        return commandeMapper.toResponse(commandeRepository.save(commande));
    }

    // ------------------------------------------------------------- livraison

    @Override
    public DeliveryResponse deliver(Long commandeId, DeliveryRequest request) {
        Commande commande = find(commandeId);
        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new ConflictException("La commande " + commande.getNumero() + " est deja livree");
        }
        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new InvalidOperationException("Une commande annulee ne peut pas etre livree");
        }
        if (commande.getEtape(TypeEtape.CONTROLE_QUALITE) == null) {
            throw new BusinessException("Livraison impossible : la commande n'a pas encore ete controlee");
        }
        ControleQualite controle = commande.getControleQualite();
        if (controle == null) {
            throw new BusinessException("Livraison impossible : le controle qualite est obligatoire "
                    + "avant toute livraison");
        }
        if (!controle.isConforme()) {
            throw new InvalidOperationException("Livraison impossible : la commande est NON CONFORME");
        }
        EtapeProduction finition = commande.getEtape(TypeEtape.FINITION);
        if (finition == null || !finition.isTerminee()) {
            throw new InvalidOperationException("Livraison impossible : la production n'est pas terminee");
        }
        if (commande.getQuantiteProduiteTotale() <= 0) {
            throw new BusinessException("Livraison impossible : aucune quantite n'a ete produite");
        }
        if (livraisonRepository.findByCommandeId(commandeId).isPresent()) {
            throw new ConflictException("Une livraison existe deja pour la commande " + commande.getNumero());
        }

        Livraison livraison = new Livraison(commande, request.getReceptionnaire(), LocalDate.now(),
                request.getBonLivraison(), request.getCommentaire());
        livraison.setQuantiteLivree(commande.getQuantiteProduiteTotale());
        Livraison saved = livraisonRepository.save(livraison);

        commande.setStatut(StatutCommande.LIVREE);
        commande.setDateLivraisonReelle(livraison.getDateLivraison());

        EtapeProduction etapeLivraison = commande.getEtape(TypeEtape.LIVRAISON);
        etapeLivraison.setStatut(StatutEtape.TERMINEE);
        etapeLivraison.setDateDebut(OptionalDate(etapeLivraison.getDateDebut()));
        etapeLivraison.setDateFinReelle(LocalDateTime.now());
        etapeLivraison.setResponsable(currentUser.currentOrNull());
        etapeLivraison.setQuantiteProduite(commande.getQuantiteProduiteTotale());
        etapeLivraison.setCommentaire("Livraison effectuee - bon " + safe(request.getBonLivraison()));

        commandeRepository.save(commande);
        return commandeMapper.toDeliveryResponse(saved);
    }

    @Override
    public OrderResponse cancel(Long commandeId, String motif) {
        Commande commande = find(commandeId);
        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new InvalidOperationException("Une commande livree ne peut pas etre annulee");
        }
        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new ConflictException("La commande " + commande.getNumero() + " est deja annulee");
        }
        commande.setStatut(StatutCommande.ANNULEE);
        commande.setCommentaire(motif == null || motif.isBlank()
                ? commande.getCommentaire()
                : commande.getCommentaire() + " | Annulation : " + motif);
        for (EtapeProduction etape : commande.getEtapes()) {
            if (!etape.isTerminee() && etape.getStatut() == StatutEtape.EN_COURS) {
                etape.setStatut(StatutEtape.BLOQUEE);
            }
        }
        return commandeMapper.toResponse(commandeRepository.save(commande));
    }

    // ---------------------------------------------------------------- helpers

    /**
     * Verifie puis applique les quantites declarees pour une etape de production.
     * Regle metier : impossible de produire plus que la quantite commandee, ni plus que
     * la quantite reellement produite a l'etape precedente.
     */
    private int appliquerQuantites(Commande commande, TypeEtape type, CompleteStepRequest request) {
        List<CompleteStepRequest.StepLineQuantity> declarations =
                request != null ? request.getQuantites() : null;
        if (declarations == null || declarations.isEmpty()) {
            throw new BusinessException("Les quantites produites par ligne sont obligatoires");
        }

        Map<Long, Integer> parLigne = new LinkedHashMap<>();
        for (CompleteStepRequest.StepLineQuantity declaration : declarations) {
            if (parLigne.putIfAbsent(declaration.getLigneId(), declaration.getQuantite()) != null) {
                throw new BusinessException("La ligne " + declaration.getLigneId()
                        + " est declaree plusieurs fois");
            }
        }

        List<LigneCommande> lignes = commande.getLignes();
        for (Long ligneId : parLigne.keySet()) {
            boolean connue = lignes.stream().anyMatch(l -> l.getId().equals(ligneId));
            if (!connue) {
                throw ResourceNotFoundException.of("Ligne de commande", ligneId);
            }
        }
        for (LigneCommande ligne : lignes) {
            if (!parLigne.containsKey(ligne.getId())) {
                throw new BusinessException("Quantite manquante pour la ligne " + ligne.getId());
            }
        }

        // Verification avant toute ecriture : on travaille sur une copie des valeurs.
        List<Integer> valeurs = new ArrayList<>();
        for (LigneCommande ligne : lignes) {
            int quantite = parLigne.get(ligne.getId());
            if (quantite < 0) {
                throw new BusinessException("La quantite produite ne peut pas etre negative");
            }
            if (quantite > ligne.getQuantite()) {
                throw new BusinessException("Quantite produced invalide sur la ligne " + ligne.getId()
                        + " : " + quantite + " > " + ligne.getQuantite()
                        + " (quantite commandee) - " + ligne.getProduit().getCode());
            }
            if (quantite > ligne.getQuantiteProduite()) {
                throw new BusinessException("Quantite produite invalide sur la ligne " + ligne.getId()
                        + " : impossible de produire " + quantite + " a l'etape " + type.getLibelle()
                        + " alors que l'etape precedente a produit " + ligne.getQuantiteProduite()
                        + " unites - " + ligne.getProduit().getCode());
            }
            valeurs.add(quantite);
        }

        for (int i = 0; i < lignes.size(); i++) {
            lignes.get(i).setQuantiteProduite(valeurs.get(i));
        }
        return valeurs.stream().mapToInt(Integer::intValue).sum();
    }

    /** Les quantites ne peuvent pas augmenter entre deux etapes successives. */
    private void verifierQuantitiesDisponibles(Commande commande, TypeEtape type) {
        for (LigneCommande ligne : commande.getLignes()) {
            if (ligne.getQuantiteProduite() <= 0) {
                throw new BusinessException("Aucune quantite disponible pour " + type.getLibelle()
                        + " : la ligne " + ligne.getId() + " (" + ligne.getProduit().getCode()
                        + ") n'a rien produit a l'etape precedente");
            }
        }
    }

    private AppUser resolveUser(Long userId) {
        if (userId != null) {
            return appUserRepository.findById(userId)
                    .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", userId));
        }
        return currentUser.currentOrNull();
    }

    private Commande find(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Commande", id));
        commande.getLignes().size();
        commande.getEtapes().size();
        commande.getControleQualite();
        return commande;
    }

    private static LocalDateTime OptionalDate(LocalDateTime date) {
        return date != null ? date : LocalDateTime.now();
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "non renseigne" : value;
    }
}