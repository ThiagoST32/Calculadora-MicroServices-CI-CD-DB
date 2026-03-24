package com.trevisan.CalculadoraMicroServicesDB.Dtos;

import com.trevisan.CalculadoraMicroServicesDB.Domain.Enums.TipoDeOperacao;

public record OperationRequestPersistDTO(String valueOne, String valueTwo, String result, TipoDeOperacao tipoDeOperacao) {
}
