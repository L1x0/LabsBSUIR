package by.astakhau.bonuslab.service;

import by.astakhau.bonuslab.data.Client;
import by.astakhau.bonuslab.data.ClientRepository;
import by.astakhau.bonuslab.data.Truck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public List<Client> getAllClients() {
        Iterable<Client> clientsIterable = clientRepository.findAll();
        return StreamSupport.stream(clientsIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public void deleteByOrderId(Long orderId) {
        clientRepository.deleteByOrderId(orderId);
    }
}