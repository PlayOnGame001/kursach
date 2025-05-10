package testservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.station.entity.Client;
import org.station.repository.ClientRepository;
import org.station.service.ClientService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestClientService {

    private ClientRepository clientRepository;
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientRepository = Mockito.mock(ClientRepository.class);
        clientService = new ClientService(clientRepository);
    }

    @Test
    void testAddClient() {
        Client client = new Client();
        clientService.addClient(client);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testRemoveClient() {
        long id = 1L;
        assertTrue(clientService.removeClient(id));
        verify(clientRepository).deleteById(id);
    }

    @Test
    void testGetClientById() {
        long id = 1L;
        Client client = new Client();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        assertTrue(clientService.getClientById(id).isPresent());
    }

    @Test
    void testGetClientsByProblem() {
        when(clientRepository.findClientsByProblem("Гальма")).thenReturn(List.of(new Client()));
        assertEquals(1, clientService.getClientsByProblem("Гальма").size());
    }

    @Test
    void testGetCommonProblem() {
        Client c1 = new Client(); c1.setProblem("Двигун");
        Client c2 = new Client(); c2.setProblem("Двигун");
        Client c3 = new Client(); c3.setProblem("Гальма");

        when(clientRepository.findAll()).thenReturn(List.of(c1, c2, c3));
        assertEquals("Двигун", clientService.getCommonProblem());
    }
}
