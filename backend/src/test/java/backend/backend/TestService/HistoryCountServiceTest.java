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
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HistoryCountServiceTest {

    @InjectMocks
    private HistoryCountService historyCountService;

    @Mock
    private HistoryCountRepository historyCountRepository;

    @Mock
    private ClientRepository clientRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddHistoryCount() {
        HistoryCountEntity historyCount = new HistoryCountEntity();
        historyCount.setChange(100);
        historyCount.setChangeDate(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        when(historyCountRepository.save(any(HistoryCountEntity.class))).thenReturn(historyCount);

        HistoryCountEntity result = historyCountService.addHistoryCount(historyCount);

        assertNotNull(result);
        assertEquals(100, result.getChange());
        assertNotNull(result.getChangeDate());
        verify(historyCountRepository, times(1)).save(historyCount);
    }

    @Test
    public void testGetHistoryCount() {
        long id = 1L;
        HistoryCountEntity historyCount = new HistoryCountEntity();
        historyCount.setId(id);
        when(historyCountRepository.findById(id)).thenReturn(java.util.Optional.of(historyCount));

        HistoryCountEntity result = historyCountService.getHistoryCount(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(historyCountRepository, times(1)).findById(id);
    }

    @Test
    public void testGetHistoryCount_NotFound() {
        long id = 1L;
        when(historyCountRepository.findById(id)).thenReturn(java.util.Optional.empty());

        HistoryCountEntity result = historyCountService.getHistoryCount(id);

        assertNull(result);
        verify(historyCountRepository, times(1)).findById(id);
    }

    @Test
    public void testR71_SufficientChange() {
        long clientId = 1L;
        int amount = 1000;
        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        HistoryCountEntity entry1 = new HistoryCountEntity();
        entry1.setChange(150);
        entry1.setClientid(clientId);
        historyCounts.add(entry1);
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        boolean result = historyCountService.R71(clientId, amount);

        assertTrue(result);
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
    }

    @Test
    public void testR71_InsufficientChange() {
        long clientId = 1L;
        int amount = 1000;
        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        HistoryCountEntity entry1 = new HistoryCountEntity();
        entry1.setChange(50);
        entry1.setClientid(clientId);
        historyCounts.add(entry1);
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        boolean result = historyCountService.R71(clientId, amount);

        assertFalse(result);
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
    }

    /*

    @Test
    public void testR72_MoneyNotExceeded() {
        long clientId = 1L;
        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(new HistoryCountEntity(1, clientId, 100, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        historyCounts.add(new HistoryCountEntity(2, clientId, 50, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        boolean result = historyCountService.R72(clientId);

        assertTrue(result);
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
    }
*/
    @Test
    public void testR72_MoneyExceeded() {
        long clientId = 1L;
        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(new HistoryCountEntity(1, clientId, 200, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        historyCounts.add(new HistoryCountEntity(2, clientId, 150, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        boolean result = historyCountService.R72(clientId);

        assertFalse(result);
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
    }

    @Test
    public void testR73_ConditionMet() {
        long clientId = 1L;
        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(new HistoryCountEntity(1, clientId, 100, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        historyCounts.add(new HistoryCountEntity(2, clientId, 200, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        ClientEntity client = new ClientEntity();
        client.setSalary(1000);
        when(clientRepository.findById(clientId)).thenReturn(client);

        boolean result = historyCountService.R73(clientId);

        assertTrue(result);
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    public void testR73_ConditionNotMet() {
        long clientId = 1L;
        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(new HistoryCountEntity(1, clientId, 10, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        ClientEntity client = new ClientEntity();
        client.setSalary(1000);
        when(clientRepository.findById(clientId)).thenReturn(client);

        boolean result = historyCountService.R73(clientId);

        assertFalse(result);
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    public void testR74_OlderLessThan2() {
        long clientId = 1L;
        int older = 1;
        int amount = 1000;
        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(new HistoryCountEntity(1, clientId, 250, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        boolean result = historyCountService.R74(clientId, older, amount);

        assertTrue(result);
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
    }

    @Test
    public void testR74_OlderMoreThan2() {
        long clientId = 1L;
        int older = 3;
        int amount = 1000;
        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(new HistoryCountEntity(1, clientId, 100, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        boolean result = historyCountService.R74(clientId, older, amount);

        assertFalse(result);
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
    }
     /*

    @Test
    public void testR75() {
        long clientId = 1L;
        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        historyCounts.add(new HistoryCountEntity(1, clientId, -50, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        historyCounts.add(new HistoryCountEntity(2, clientId, 200, Timestamp.valueOf(LocalDate.now().atStartOfDay())));
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        boolean result = historyCountService.R75(clientId);

        assertTrue(result);
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
    }


    @Test
    public void testR7Complete_AllConditionsMet() {
        long clientId = 1L;
        int older = 1;
        int amount = 1000;

        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(new ArrayList<>());

        // Mocks para las otras condiciones
        when(historyCountService.R71(clientId, amount)).thenReturn(true);
        when(historyCountService.R72(clientId)).thenReturn(true);
        when(historyCountService.R73(clientId)).thenReturn(true);
        when(historyCountService.R74(clientId, older, amount)).thenReturn(true);
        when(historyCountService.R75(clientId)).thenReturn(true);

        boolean result = historyCountService.R7Complete(clientId, older, amount);

        assertTrue(result);
        verify(historyCountService, times(1)).R71(clientId, amount);
    }

    @Test
    public void testR7Complete_OneConditionNotMet() {
        long clientId = 1L;
        int older = 1;
        int amount = 1000;

        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(new ArrayList<>());

        // Mocks para las otras condiciones
        when(historyCountService.R71(clientId, amount)).thenReturn(false);
        when(historyCountService.R72(clientId)).thenReturn(true);
        when(historyCountService.R73(clientId)).thenReturn(true);
        when(historyCountService.R74(clientId, older, amount)).thenReturn(true);
        when(historyCountService.R75(clientId)).thenReturn(true);

        boolean result = historyCountService.R7Complete(clientId, older, amount);

        assertFalse(result);
        verify(historyCountService, times(1)).R71(clientId, amount);
    }



    @Test
    public void testAddHistoryCount_NullInput() {
        HistoryCountEntity result = historyCountService.addHistoryCount(null);

        assertNull(result);
        verify(historyCountRepository, never()).save(any());
    }

    @Test
    public void testR71_NegativeAmount() {
        long clientId = 1L;
        int amount = -1000; // Cantidad negativa
        List<HistoryCountEntity> historyCounts = new ArrayList<>();
        HistoryCountEntity entry1 = new HistoryCountEntity();
        entry1.setChange(150);
        historyCounts.add(entry1);
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(historyCounts);

        boolean result = historyCountService.R71(clientId, amount);

        assertFalse(result); // Debería ser falso para cantidades negativas
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
    }

    @Test
    public void testR72_NoHistoryCount() {
        long clientId = 1L;
        when(historyCountRepository.findAllByClientid(clientId)).thenReturn(new ArrayList<>());

        boolean result = historyCountService.R72(clientId);

        assertFalse(result); // No debería exceder si no hay entradas
        verify(historyCountRepository, times(1)).findAllByClientid(clientId);
    }
    */

}
