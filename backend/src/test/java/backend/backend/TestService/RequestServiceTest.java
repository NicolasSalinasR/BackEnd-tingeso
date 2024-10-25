package backend.backend.TestService;


import backend.backend.Entity.RequestEntity;
import backend.backend.Repository.RequestRepository;
import backend.backend.Service.RequestService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RequestServiceTest {
    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRequest() {
        String typeOfRequest = "Loan";
        int stage = 1;
        int amount = 5000;
        int termYears = 5;
        long clientId = 12345L;
        byte[] pdfDocument = new byte[]{1, 2, 3};

        RequestEntity request = new RequestEntity();
        request.setTypeOfRequest(typeOfRequest);
        request.setStage(stage);
        request.setClientId(clientId);
        request.setAmount(amount);
        request.setYearTerm(termYears);
        request.setPdfDocument(pdfDocument);

        when(requestRepository.save(any(RequestEntity.class))).thenReturn(request);

        RequestEntity createdRequest = requestService.createRequest(typeOfRequest, stage, amount, termYears, clientId, pdfDocument);

        assertNotNull(createdRequest);
        assertEquals(typeOfRequest, createdRequest.getTypeOfRequest());
        verify(requestRepository, times(1)).save(any(RequestEntity.class));
    }

    @Test
    void testUpdateStage_Success() {
        long requestId = 1L;
        int newStage = 2;
        RequestEntity request = new RequestEntity();
        request.setId(requestId);
        request.setStage(1);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        requestService.updateStage(requestId, newStage);

        assertEquals(newStage, request.getStage());
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void testUpdateStage_RequestNotFound() {
        long requestId = 1L;
        int newStage = 2;

        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            requestService.updateStage(requestId, newStage);
        });

        assertEquals("Request with ID " + requestId + " not found.", exception.getMessage());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void testGetRequestById_Success() {
        long requestId = 1L;
        RequestEntity request = new RequestEntity();
        request.setId(requestId);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        RequestEntity foundRequest = requestService.getRequestById(requestId);

        assertNotNull(foundRequest);
        assertEquals(requestId, foundRequest.getId());
    }

    @Test
    void testGetRequestById_NotFound() {
        long requestId = 1L;

        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            requestService.getRequestById(requestId);
        });

        assertEquals("Request with ID " + requestId + " not found.", exception.getMessage());
    }

    @Test
    void testGetAllRequests() {
        List<RequestEntity> requests = new ArrayList<>();
        requests.add(new RequestEntity());
        requests.add(new RequestEntity());

        when(requestRepository.findAll()).thenReturn(requests);

        List<RequestEntity> allRequests = requestService.getAllRequests();

        assertNotNull(allRequests);
        assertEquals(2, allRequests.size());
        verify(requestRepository, times(1)).findAll();
    }

    @Test
    void testGetAllRequestsByClientId() {
        long clientId = 12345L;
        List<RequestEntity> requests = new ArrayList<>();
        requests.add(new RequestEntity());
        requests.add(new RequestEntity());

        when(requestRepository.findAllByClientId(clientId)).thenReturn(requests);

        List<RequestEntity> clientRequests = requestService.GetAllRequestsByClientId(clientId);

        assertNotNull(clientRequests);
        assertEquals(2, clientRequests.size());
        verify(requestRepository, times(1)).findAllByClientId(clientId);
    }

    @Test
    void testGetAllRequests_Empty() {
        when(requestRepository.findAll()).thenReturn(new ArrayList<>());

        List<RequestEntity> allRequests = requestService.getAllRequests();

        assertNotNull(allRequests);
        assertTrue(allRequests.isEmpty());
        verify(requestRepository, times(1)).findAll();
    }
}
