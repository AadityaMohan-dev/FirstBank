package com.aadi.bank.the_first_bank.services;

import com.aadi.bank.the_first_bank.dto.BankResponse;
import com.aadi.bank.the_first_bank.dto.CreditDebitRequest;
import com.aadi.bank.the_first_bank.dto.EnquiryRequest;
import com.aadi.bank.the_first_bank.dto.UserRequest;

public interface UserSvc {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse bankEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
}
