package com.aadi.bank.the_first_bank.controller;

import com.aadi.bank.the_first_bank.entity.Transaction;
import com.aadi.bank.the_first_bank.services.BankStatement;
import com.itextpdf.text.DocumentException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bankStatement")
public class TransactionsController {
    private BankStatement bankStatement;
    @GetMapping
    public List<Transaction>  generateBankStatement(@RequestParam String accountNumber,@RequestParam String startDate, @RequestParam String endDate) throws DocumentException {
            return bankStatement.generateStatement(accountNumber,startDate,endDate);
    }
}
