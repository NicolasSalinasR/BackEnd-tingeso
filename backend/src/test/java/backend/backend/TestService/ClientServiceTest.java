package backend.backend.TestService;

import backend.backend.Entity.ClientEntity;
import backend.backend.Repository.ClientRepository;
import backend.backend.Service.ClientService;
import backend.backend.Service.HistoryCountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private HistoryCountService historyCountService;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for createClient method (P2)
    @Test
    public void testCreateClient() {
        ClientEntity client = new ClientEntity();
        when(clientRepository.save(client)).thenReturn(client);

        ClientEntity result = clientService.createClient(client);

        assertNotNull(result);
        verify(clientRepository, times(1)).save(client);
    }

    // Test for getClientById method
    @Test
    public void testGetClientById() {
        long id = 1L;
        ClientEntity client = new ClientEntity();
        when(clientRepository.findById(id)).thenReturn(client);

        ClientEntity result = clientService.getClientById(id);

        assertNotNull(result);
        assertEquals(client, result);
        verify(clientRepository, times(1)).findById(id);
    }

    // Test for getClient method
    @Test
    public void testGetClient() {
        String rut = "12345678-9";
        ClientEntity client = new ClientEntity();
        when(clientRepository.findByRut(rut)).thenReturn(client);

        ClientEntity result = clientService.getClient(rut);

        assertNotNull(result);
        assertEquals(client, result);
        verify(clientRepository, times(1)).findByRut(rut);
    }

    // Test for simulateLoanAmount method (P1)
    @Test
    public void testSimulateLoanAmount() {
        int amount = 100000;
        int termYears = 20;
        double annualInterest = 0.045; // 4.5%

        int result = clientService.simulateLoanAmount(amount, termYears, annualInterest);

        assertTrue(result > 0);
    }

    // Test for totalCostP6 method (P6)
    @Test
    public void testTotalCostP6() {
        int amount = 100000;
        int termYears = 20;
        double annualInterest = 0.045;
        double lifeInsurance = 0.001; // 0.1%
        double fireInsurance = 1000;
        double adminFee = 0.002; // 0.2%

        int result = clientService.totalCostP6(amount, termYears, annualInterest, lifeInsurance, fireInsurance, adminFee);

        assertTrue(result > 0);
    }

    // Test for R1 method
    @Test
    public void testR1() {
        long clientId = 1L;
        int amount = 100000;
        int termYears = 20;
        double annualInterest = 0.045;
        ClientEntity client = new ClientEntity();
        client.setSalary(500000);
        when(clientRepository.findById(clientId)).thenReturn(client);

        boolean result = clientService.R1(clientId, amount, termYears, annualInterest);

        assertFalse(result); // Suponiendo que el préstamo no excede el 35%
    }

    // Test for R2 method
    @Test
    public void testR2() {
        long clientId = 1L;
        ClientEntity client = new ClientEntity();
        client.setDicom(false); // Sin mal historial crediticio
        when(clientRepository.findById(clientId)).thenReturn(client);

        boolean result = clientService.R2(clientId);

        assertFalse(result); // Suponiendo que no tiene mal historial
    }

    // Test for R3 method
    @Test
    public void testR3() {
        long clientId = 1L;
        ClientEntity client = new ClientEntity();
        client.setJobTenure(2); // Más de un año de empleo
        when(clientRepository.findById(clientId)).thenReturn(client);

        boolean result = clientService.R3(clientId);

        assertTrue(result); // Suponiendo que ha trabajado más de un año
    }

    // Test for R4 method
    @Test
    public void testR4() {
        long clientId = 1L;
        int debt = 50000;
        int amount = 100000;
        ClientEntity client = new ClientEntity();
        client.setSalary(500000);
        when(clientRepository.findById(clientId)).thenReturn(client);

        boolean result = clientService.R4(clientId, debt, amount);

        assertTrue(result); // Suponiendo que la relación deuda/ingreso es menor al 50%
    }

    // Test for R5 method
    @Test
    public void testR5() {
        int type = 1; // Primera casa
        int cost = 800000;
        int loan = 600000;

        boolean result = clientService.R5(type, cost, loan);

        assertTrue(result); // Suponiendo que el préstamo cubre el costo suficiente
    }

    // Test for R6 method
    @Test
    public void testR6() {
        long clientId = 1L;
        ClientEntity client = new ClientEntity();
        client.setAge(65); // Menor de 70
        when(clientRepository.findById(clientId)).thenReturn(client);

        boolean result = clientService.R6(clientId);

        assertTrue(result); // Suponiendo que el cliente es menor de 70
    }

    // Test for Rcomplete method
    @Test
    public void testRcomplete() {
        long clientId = 1L;
        int type = 1;
        int loan = 100000;
        int debt = 50000;
        int amount = 100000;
        int older = 65;
        int termYears = 20;
        double annualInterest = 0.045;

        // Mockear el cliente para que sea devuelto por el repositorio
        ClientEntity mockClient = new ClientEntity();
        mockClient.setSalary(500000); // Ajusta el salario según tu lógica
        mockClient.setAge(older); // Establece la edad si es necesario
        when(clientRepository.findById(clientId)).thenReturn(mockClient);

        when(historyCountService.R7Complete(clientId, older, amount)).thenReturn(true);

        List<Boolean> result = clientService.Rcomplete(clientId, type, loan, debt, amount, older, termYears, annualInterest);

        assertEquals(7, result.size());
        assertTrue(result.get(6)); // Suponiendo que R7 es true
    }
}
