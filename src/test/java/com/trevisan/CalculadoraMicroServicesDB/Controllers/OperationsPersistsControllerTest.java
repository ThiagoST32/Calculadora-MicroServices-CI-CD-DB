package com.trevisan.CalculadoraMicroServicesDB.Controllers;

import com.trevisan.CalculadoraMicroServicesDB.Domain.Enums.TipoDeOperacao;
import com.trevisan.CalculadoraMicroServicesDB.Domain.Operations;
import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationsRequestDTO;
import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationsResponseDTO;
import com.trevisan.CalculadoraMicroServicesDB.Mappers.OperationMappers;
import com.trevisan.CalculadoraMicroServicesDB.Repositories.OperationRepository;
import com.trevisan.CalculadoraMicroServicesDB.Services.OperationPersistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationsPersistsControllerTest {

    @Mock
    private OperationRepository repository;

    @Mock
    private OperationMappers mappers;

    @InjectMocks
    private OperationPersistService persistService;

    @InjectMocks
    private OperationsPersistsController controller;

    private OperationsRequestDTO validRequest;

    @Captor
    private ArgumentCaptor<Operations> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validRequest = new OperationsRequestDTO(
                "5", "5", "10", TipoDeOperacao.SOMA
        );
        persistService = new OperationPersistService(repository, mappers);
        controller = new OperationsPersistsController(persistService);
    }

    @Test
    @DisplayName("Should verify if persisted operation is called once time")
    void persistOperation_callsServiceSave() {
        controller.persistOperation(validRequest);
        verify(repository, times(1)).save(captor.capture());
    }

    @Test
    @DisplayName("Should return a body with all operations persisted")
    void getAllOperations_returnsOkWithBody() {
        persistService.saveOperationOnDB(validRequest);
        verify(repository).save(captor.capture());

        Operations persistedOperation = captor.getValue();
        when(repository.findAll()).thenAnswer(operations -> List.of(persistedOperation));

        OperationsResponseDTO expected = new OperationsResponseDTO(
                persistedOperation.getValueOne(),
                persistedOperation.getValueTwo(),
                persistedOperation.getResult(),
                persistedOperation.getTipoDeOperacao()
        );

        when(mappers.mapToOperationsResponse(persistedOperation)).thenReturn(expected);
        ResponseEntity<List<OperationsResponseDTO>> response = controller.getAllOperations();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(expected, response.getBody().getFirst());
    }

    @Test
    @DisplayName("Should return a body with previous operations persisted")
    void getPreviousOperationPersisted_returnsOkWithBody() {
        OperationsResponseDTO resp = new OperationsResponseDTO("1", "2", "3", null);
        when(persistService.getPreviousOperations()).thenReturn(resp);

        ResponseEntity<OperationsResponseDTO> response = controller.getPreviousOperationPersisted();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(resp, response.getBody());
    }
}

