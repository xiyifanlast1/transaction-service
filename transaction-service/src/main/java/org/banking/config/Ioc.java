package org.banking.config;

import org.banking.infra.IRepository;
import org.banking.infra.implement.MapRepository;
import org.banking.service.entities.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Ioc {
    @Autowired
    MapRepository<TransactionEntity> transactionRepository;
    @Bean
    public IRepository<TransactionEntity> getTransactionRepository(){
        transactionRepository.init("transaction");
        return transactionRepository;
    }
}
