package org.station.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.station.entity.Client;
import org.station.repository.ClientRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public void addClient(Client client) {
        clientRepository.save(client);
    }

    public boolean removeClient(long ID) {
        clientRepository.deleteById(ID);
        return true;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(long id) {
        return clientRepository.findById(id);
    }

    public List<Client> getClientsByProblem(String problem) {
        return clientRepository.findClientsByProblem(problem);
    }

    public List<Client> getClientsByCarBrand(String brand) {
        return clientRepository.findClientsByCarBrand(brand);
    }

    public String getCommonProblem() {
        List<String> problems = clientRepository.findAll()
                .stream()
                .map(Client::getProblem)
                .toList();

        return problems.isEmpty() ? "Жодної проблеми не знайдено" :
                Collections.max(problems, Comparator.comparingInt(a -> Collections.frequency(problems, a)));
    }

    public Optional<Client> getClientByCarId(Long carId) {
        return clientRepository.findAll().stream()
                .filter(c -> c.getCar() != null && c.getCar().getId() == carId)
                .findFirst();
    }

}
