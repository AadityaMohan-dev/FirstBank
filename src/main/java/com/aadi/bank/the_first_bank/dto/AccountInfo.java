package com.aadi.bank.the_first_bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
    @Schema(name = "user account Number")
    private String accountNumber;
    @Schema(name = "user account balance")
    private BigDecimal accountBalance;
    @Schema(name = "user account Name")
    private String accountName;
    @Schema(name = "user account amount")
    private BigDecimal amount;
}
