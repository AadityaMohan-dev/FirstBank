package com.aadi.bank.the_first_bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetail {
    private String recipient;
    private String messageBody;
    private String subject;
    private String attachment;
}
