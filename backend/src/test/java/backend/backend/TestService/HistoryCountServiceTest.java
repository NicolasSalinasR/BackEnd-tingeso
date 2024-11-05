package backend.backend.TestService;

import backend.backend.Entity.ClientEntity;
import backend.backend.Entity.HistoryCountEntity;
import backend.backend.Repository.ClientRepository;
import backend.backend.Repository.HistoryCountRepository;
import backend.backend.Service.HistoryCountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HistoryCountServiceTest {

    @Mock
    private HistoryCountRepository historyCountRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private HistoryCountService historyCountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    private HistoryCountEntity createHistoryCountEntity(int change, LocalDate date) {
        HistoryCountEntity entity = new HistoryCountEntity();
        entity.setChange(change);
        entity.setChangeDate(java.sql.Timestamp.valueOf(date.atStartOfDay()));
        return entity;
    }


    // Tests para addHistoryCount
    @Test
    void testAddHistoryCount_Success() {
        HistoryCountEntity historyCount = new HistoryCountEntity();
        when(historyCountRepository.save(historyCount)).thenReturn(historyCount);
        HistoryCountEntity result = historyCountService.addHistoryCount(historyCount);
        assertEquals(historyCount, result);
    }

    @Test
    void testAddHistoryCount_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> historyCountService.addHistoryCount(null));
    }

    @Test
    void testAddHistoryCount_SaveFailure() {
        HistoryCountEntity historyCount = new HistoryCountEntity();
        when(historyCountRepository.save(historyCount)).thenThrow(new RuntimeException("Save failed"));
        assertThrows(RuntimeException.class, () -> historyCountService.addHistoryCount(historyCount));
    }

    // Tests para getHistoryCount
    @Test
    void testGetHistoryCount_ExistingId() {
        Long id = 1L;
        HistoryCountEntity historyCount = new HistoryCountEntity();
        when(historyCountRepository.findById(id)).thenReturn(Optional.of(historyCount));
        HistoryCountEntity result = historyCountService.getHistoryCount(id);
        assertEquals(historyCount, result);
    }

    @Test
    void testGetHistoryCount_NonExistingId() {
        Long id = 1L;
        when(historyCountRepository.findById(id)).thenReturn(Optional.empty());
        HistoryCountEntity result = historyCountService.getHistoryCount(id);
        assertNull(result);
    }

    @Test
    void testGetHistoryCount_NullId() {
        assertThrows(IllegalArgumentException.class, () -> historyCountService.getHistoryCount(null));
    }

    // Tests para R71
    @Test
    void testR71_ConditionTrue() {
        Long clientId = 1L;
        int amount = 1000;
        HistoryCountEntity h1 = new HistoryCountEntity();
        h1.setChange(100);
        HistoryCountEntity h2 = new HistoryCountEntity();
        h2.setChange(50);
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(Arrays.asList(h1, h2));
        assertTrue(historyCountService.R71(clientId, amount));
    }

    @Test
    void testR71_ConditionFalse() {
        Long clientId = 1L;
        int amount = 1000;
        HistoryCountEntity h1 = new HistoryCountEntity();
        h1.setChange(20);
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(List.of(h1));
        assertFalse(historyCountService.R71(clientId, amount));
    }

    @Test
    void testR71_EmptyHistory() {
        Long clientId = 1L;
        int amount = 1000;
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(List.of());
        assertFalse(historyCountService.R71(clientId, amount));
    }


    @Test
    void testR72_NoTransactions() {
        Long clientId = 1L;
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(List.of());
        assertTrue(historyCountService.R72(clientId));  // No transactions should result in true
    }


    @Test
    void testR73_NoHistory() {
        Long clientId = 1L;
        ClientEntity client = new ClientEntity();
        client.setId(clientId);  // Asigna el ID del cliente
        client.setSalary(10000);  // Salario del cliente

        // Cliente sin historial de transacciones
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client)); // Devuelve un Optional
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(Collections.emptyList());

        // Aquí se llama al método que estás probando, usando la instancia real
        boolean result = historyCountService.R73(clientId);

        assertFalse(result);
    }


    @Test
    void testR73_BothConditionsFalse() {
        Long clientId = 1L;
        ClientEntity client = new ClientEntity();
        client.setId(clientId);  // Asigna el ID del cliente
        client.setSalary(10000);  // Salario del cliente

        // Depósitos que no suman al menos el 5% del salario
        HistoryCountEntity h1 = new HistoryCountEntity();
        h1.setChange(100);  // Mucho menos del 5% del salario
        h1.setChangeDate(java.sql.Timestamp.valueOf(LocalDate.now().minusMonths(2).atStartOfDay()));

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client)); // Devuelve un Optional
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(List.of(h1));

        // Simula la ausencia de depósitos trimestrales
        HistoryCountService historyCountServiceMock = Mockito.mock(HistoryCountService.class);
        when(historyCountServiceMock.hasQuarterlyDeposits(anyList())).thenReturn(false);

        // Aquí se llama al método que estás probando
        boolean result = historyCountService.R73(clientId);

        assertFalse(result);
    }

    

    @Test
    public void testR74_OlderGreaterThanTwo_ConditionMet() {
        long clientId = 1L;
        int older = 3;
        int amount = 1000;

        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(createHistoryCountEntity(200, LocalDate.now().minusMonths(1))); // suma total 200

        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        assertTrue(historyCountService.R74(clientId, older, amount));
    }

    @Test
    public void testR74_OlderLessThanTwo_ConditionMet() {
        long clientId = 2L;
        int older = 1;
        int amount = 1000;

        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(createHistoryCountEntity(250, LocalDate.now().minusMonths(2))); // suma total 250

        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        assertTrue(historyCountService.R74(clientId, older, amount));
    }

    @Test
    public void testR74_ConditionNotMet() {
        long clientId = 3L;
        int older = 3;
        int amount = 5000;

        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(createHistoryCountEntity(200, LocalDate.now().minusMonths(3))); // suma total insuficiente

        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        assertFalse(historyCountService.R74(clientId, older, amount));
    }
    @Test
    public void testR75_PositiveCase() {
        long clientId = 1L;

        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(createHistoryCountEntity(100, LocalDate.now().minusMonths(7))); // fuera de los 6 meses
        historyCounts.add(createHistoryCountEntity(-20, LocalDate.now().minusMonths(2))); // retiro menor al 30%

        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        assertTrue(historyCountService.R75(clientId));
    }

    @Test
    public void testR75_NegativeCase() {
        long clientId = 2L;

        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(createHistoryCountEntity(-50, LocalDate.now().minusMonths(3))); // retiro mayor al 30%

        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        assertFalse(historyCountService.R75(clientId));
    }

    @Test
    public void testR75_NoWithdrawalsInLast6Months() {
        long clientId = 3L;

        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(createHistoryCountEntity(100, LocalDate.now().minusMonths(7))); // fuera de los 6 meses

        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        assertTrue(historyCountService.R75(clientId));
    }

}
