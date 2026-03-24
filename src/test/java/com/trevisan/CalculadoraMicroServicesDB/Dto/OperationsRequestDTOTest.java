package com.trevisan.CalculadoraMicroServicesDB.Dto;

import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationRequestPersistDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OperationsRequestDTOTest {

    @Test
    @DisplayName("Should return provided values")
    void record_getters_returnProvidedValues() {
        OperationRequestPersistDTO dto = new OperationRequestPersistDTO("10", "20", "30", null);

        assertEquals("10", dto.valueOne());
        assertEquals("20", dto.valueTwo());
        assertEquals("30", dto.result());
        assertNull(dto.tipoDeOperacao());
    }

    @Test
    @DisplayName("Should verify hashcode between DTOs")
    void equals_and_hashcode_work() {
        OperationRequestPersistDTO a = new OperationRequestPersistDTO("1", "2", "3", null);
        OperationRequestPersistDTO b = new OperationRequestPersistDTO("1", "2", "3", null);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}

