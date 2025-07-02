package org.banking.controller;

import org.banking.infra.base.ServiceResult;
import org.banking.service.ITransactionService;
import org.banking.service.dtos.CreateTransactionInput;
import org.banking.service.dtos.Transaction;
import org.banking.service.dtos.UpdateTransactionInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {
    @Autowired
    ITransactionService service;
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResult<Transaction>> get(@PathVariable String id)
    {
        return new ResponseEntity<>(service.get(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ServiceResult<List<Transaction>>> find(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return new ResponseEntity<>(service.find(page, size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ServiceResult<Transaction>> create(@RequestBody CreateTransactionInput input) {
        return new ResponseEntity<>(service.create(input), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ServiceResult> update(@RequestBody UpdateTransactionInput input) {
        return new ResponseEntity<>(service.update(input), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResult> deleteTransaction(@PathVariable String id) {
        return new ResponseEntity<>(service.delete(id), HttpStatus.OK);
    }
}
