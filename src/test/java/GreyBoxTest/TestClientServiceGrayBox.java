package GreyBoxTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.station.entity.Client;
import org.station.service.ClientService;
import org.station.repository.ClientRepository;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestClientServiceGrayBox {

    private ClientRepository mockRepository;
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        mockRepository = mock(ClientRepository.class);
        clientService = new ClientService(mockRepository);
    }

    @Test
    void testAddAndGetClient() {
        Client client = new Client("Станція A", "Іван Петренко", "123456", "Гальма не працюють", null);

        when(mockRepository.save(client)).thenReturn(client);
        when(mockRepository.findById(1L)).thenReturn(Optional.of(client));

        clientService.addClient(client);
        Optional<Client> found = clientService.getClientById(1L);

        assertTrue(found.isPresent());
        assertEquals("Іван Петренко", found.get().getFullName());

        verify(mockRepository).save(client);
        verify(mockRepository).findById(1L);
    }
}
