package com.trevisan.CalculadoraMicroServicesDB.Dtos;

import com.trevisan.CalculadoraMicroServicesDB.Domain.Enums.TipoDeOperacao;

public record OperationsResponseDTO(String valueOne, String valueTwo, String result, TipoDeOperacao tipoDeOperacao) {
}
