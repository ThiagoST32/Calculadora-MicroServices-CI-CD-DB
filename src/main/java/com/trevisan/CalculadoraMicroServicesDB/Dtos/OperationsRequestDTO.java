package com.trevisan.CalculadoraMicroServicesDB.Dtos;

import com.trevisan.CalculadoraMicroServicesDB.Domain.Enums.TipoDeOperacao;

public record OperationsRequestDTO(String valueOne, String valueTwo, String result, TipoDeOperacao tipoDeOperacao) {
}
