package com.example.textile.service.impl;

import com.example.textile.dto.request.CreateClientRequest;
import com.example.textile.dto.request.UpdateClientRequest;
import com.example.textile.dto.response.ClientResponse;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.entity.Client;
import com.example.textile.exception.BusinessException;
import com.example.textile.exception.ConflictException;
import com.example.textile.exception.ResourceNotFoundException;
import com.example.textile.mapper.ClientMapper;
import com.example.textile.repository.ClientRepository;
import com.example.textile.repository.CommandeRepository;
import com.example.textile.service.ClientService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final CommandeRepository commandeRepository;
    private final ClientMapper clientMapper;

    public ClientServiceImpl(ClientRepository clientRepository,
                             CommandeRepository commandeRepository,
                             ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.commandeRepository = commandeRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public ClientResponse create(CreateClientRequest request) {
        Client client = clientMapper.toEntity(request);
        if (clientRepository.existsByPhone(client.getPhone())) {
            throw new ConflictException("Un client avec le telephone " + client.getPhone() + " existe deja");
        }
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getById(Long id) {
        return clientMapper.toResponse(find(id));
    }

    @Override
    public ClientResponse update(Long id, UpdateClientRequest request) {
        Client client = find(id);
        if (request.getPhone() != null && !request.getPhone().isBlank()
                && clientRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new ConflictException("Un autre client utilise deja le telephone " + request.getPhone());
        }
        clientMapper.updateEntity(client, request);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Override
    public void delete(Long id) {
        Client client = find(id);
        long nombreCommandes = commandeRepository.countByClientId(id);
        if (nombreCommandes > 0) {
            throw new BusinessException("Impossible de supprimer le client : " + nombreCommandes
                    + " commande(s) lui sont associee(s). Desactivez-le plutot.");
        }
        clientRepository.delete(client);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClientResponse> search(String search, Boolean active, Pageable pageable) {
        return PageResponse.from(
                clientRepository.search(normalize(search), active, pageable),
                c -> clientMapper.toResponse(c, commandeRepository.countByClientId(c.getId())));
    }

    private Client find(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Client", id));
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}