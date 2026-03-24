package com.trevisan.CalculadoraMicroServicesDB.Services;

import com.trevisan.CalculadoraMicroServicesDB.Domain.Enums.TipoDeOperacao;
import com.trevisan.CalculadoraMicroServicesDB.Domain.Operations;
import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationRequestPersistDTO;
import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationsResponseDTO;
import com.trevisan.CalculadoraMicroServicesDB.Mappers.OperationMappers;
import com.trevisan.CalculadoraMicroServicesDB.Repositories.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationPersistServiceTest {

    @Mock
    private OperationRepository repository;

    @InjectMocks
    private OperationPersistService service;

    @Captor
    private ArgumentCaptor<Operations> captor;

    @Mock
    private OperationMappers mappers;

    private OperationRequestPersistDTO validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new OperationRequestPersistDTO(
                "5", "5", "10", TipoDeOperacao.SOMA
        );    }

    @Test
    @DisplayName("Must save operation in database when DTO is valid")
    void saveOperationOnDB() {
        service.saveOperationOnDB(validRequest);
        verify(repository).save(captor.capture());
        Operations operationsPersisted = captor.getValue();

        assertNotNull(operationsPersisted);
        assertEquals("5", operationsPersisted.getValueOne());
        assertEquals("5", operationsPersisted.getValueTwo());
        assertEquals("10", operationsPersisted.getResult());
        assertEquals(TipoDeOperacao.SOMA, operationsPersisted.getTipoDeOperacao());
    }

    @Test
    @DisplayName("Should throw exception when DTO is null")
    void saveOperationOnDB_WithNullDTO_ShouldReturnRuntimeException(){
        assertThrows(RuntimeException.class, () -> service.saveOperationOnDB(null));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should return list of all operations persisted")
    void getAllOperations() {
        service.saveOperationOnDB(validRequest);
        verify(repository).save(captor.capture());

        Operations operationPersisted = captor.getValue();
        when(repository.findAll()).thenAnswer(operation -> { return List.of(operationPersisted); });
        assertNotNull(operationPersisted);

        OperationsResponseDTO expected = new OperationsResponseDTO(
                operationPersisted.getValueOne(),
                operationPersisted.getValueTwo(),
                operationPersisted.getResult(),
                operationPersisted.getTipoDeOperacao()
        );

        when(mappers.mapToOperationsResponse(operationPersisted)).thenReturn(expected);
        List<OperationsResponseDTO> operations = service.getAllOperations();

        assertThatList(operations).isNotNull();
        assertThatList(operations).isNotEmpty();
        assertThatList(operations).hasSize(1);
    }

    @Test
    @DisplayName("Should return a empty list of operations")
    void shouldReturnEmptyList(){
        var operations = service.getAllOperations();
        assertThatList(operations).isNotNull();
        assertThatList(operations).isEmpty();
    }

    @Test
    @DisplayName("Should return previous operation persisted")
    void getPreviousOperations() {
        service.saveOperationOnDB(validRequest);
        verify(repository).save(captor.capture());

        Operations operationPersisted = captor.getValue();
        when(repository.findPreviousOperationsPersisted()).thenAnswer(operation -> { return operationPersisted; });
        assertNotNull(operationPersisted);

        OperationsResponseDTO expected = new OperationsResponseDTO(
                operationPersisted.getValueOne(),
                operationPersisted.getValueTwo(),
                operationPersisted.getResult(),
                operationPersisted.getTipoDeOperacao()
        );

        when(mappers.mapToOperationsResponse(operationPersisted)).thenReturn(expected);
        OperationsResponseDTO operations = service.getPreviousOperations();

        assertThat(operations).isNotNull();
        assertThat(operations).isSameAs(expected);
    }
}