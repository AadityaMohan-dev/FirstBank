package com.aadi.bank.the_first_bank.controller;

import com.aadi.bank.the_first_bank.dto.*;
import com.aadi.bank.the_first_bank.services.UserSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management API's")
public class UserController {
    @Autowired
    UserSvc userSvc;

    @Operation(summary = "Create new user Account", description = "Creating a new user and Assigning a User ID.")
    @ApiResponse(responseCode = "201", description = "Account Created Successfully.")
    @PostMapping
    public BankResponse createBankAccount(@RequestBody UserRequest userRequest){
        return userSvc.createAccount(userRequest);
    }


    @Operation(summary = "Balance Enquiry", description = "Given Account Number, check How much the user have")
    @ApiResponse(responseCode = "200", description = "HTTP Status code 200 SUCCESS")
    @GetMapping("/balance_enquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userSvc.bankEnquiry(request);
    }


    @Operation(summary = "Name Enquiry", description = "Name Enquiry")
    @ApiResponse(responseCode = "200", description = "HTTP Status code 200 SUCCESS")
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

    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest transferRequest){
        return userSvc.transfer(transferRequest);
    }
}
