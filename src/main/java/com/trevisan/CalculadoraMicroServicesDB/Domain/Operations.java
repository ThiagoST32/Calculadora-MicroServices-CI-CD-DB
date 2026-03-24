package com.trevisan.CalculadoraMicroServicesDB.Domain;

import com.trevisan.CalculadoraMicroServicesDB.Domain.Enums.TipoDeOperacao;
import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationRequestPersistDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "operationsId")
public class Operations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operationsId;

    private String valueOne;

    private String valueTwo;

    private TipoDeOperacao tipoDeOperacao;

    private String result;

    private LocalDateTime localDateTime = LocalDateTime.now();

    public Operations(OperationRequestPersistDTO dto){
        valueOne = dto.valueOne();
        valueTwo = dto.valueTwo();
        result = dto.result();
        tipoDeOperacao = dto.tipoDeOperacao();
    }
}
