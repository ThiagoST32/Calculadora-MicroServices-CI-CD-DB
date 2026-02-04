package com.trevisan.CalculadoraMicroServicesDB.Controllers;

import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationsRequestDTO;
import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationsResponseDTO;
import com.trevisan.CalculadoraMicroServicesDB.Services.OperationPersistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/calcRepo")
public class OperationsPersistsController {

    private final OperationPersistService persistService;

    @Autowired
    public OperationsPersistsController(OperationPersistService persistService) {
        this.persistService = persistService;
    }

    @PostMapping("/register")
    public void persistOperation(@RequestBody OperationsRequestDTO requestDTO){
        persistService.saveOperationOnDB(requestDTO);
    }

    @GetMapping
    public ResponseEntity<List<OperationsResponseDTO>>getAllOperations(){
        return new ResponseEntity<>(persistService.getAllOperations(), HttpStatus.OK);
    }

    @GetMapping("/getPreviousOperation")
    public ResponseEntity<OperationsResponseDTO>getPreviousOperationPersisted(){
        return new ResponseEntity<>(persistService.getPreviousOperations(), HttpStatus.OK);
    }
}
