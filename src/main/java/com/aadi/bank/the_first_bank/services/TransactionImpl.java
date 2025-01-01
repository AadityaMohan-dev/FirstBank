package com.aadi.bank.the_first_bank.services;

import com.aadi.bank.the_first_bank.dto.TransactionDto;
import com.aadi.bank.the_first_bank.entity.Transaction;
import com.aadi.bank.the_first_bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionImpl implements TrnsSvc{

    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public void savedTransactions(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .transactionType(transactionDto.getTransactionType())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transaction);
    }

}
