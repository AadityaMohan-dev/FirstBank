package com.aadi.bank.the_first_bank.controller;

import com.aadi.bank.the_first_bank.dto.BankResponse;
import com.aadi.bank.the_first_bank.dto.CreditDebitRequest;
import com.aadi.bank.the_first_bank.dto.EnquiryRequest;
import com.aadi.bank.the_first_bank.dto.UserRequest;
import com.aadi.bank.the_first_bank.services.UserSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserSvc userSvc;
    @PostMapping
    public BankResponse createBankAccount(@RequestBody UserRequest userRequest){
        return userSvc.createAccount(userRequest);
    }
    @GetMapping("/balance_enquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userSvc.bankEnquiry(request);
    }
    @GetMapping("/name_enquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userSvc.nameEnquiry(request);
    }
    @PutMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userSvc.creditAccount(request);
    }
    @PutMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userSvc.debitAccount(request);
    }
}
