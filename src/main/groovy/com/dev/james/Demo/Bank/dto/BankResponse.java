package com.dev.james.Demo.Bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankResponse {
    @Schema(
            name = "Response Code"
    )
    private String responseCode;
    @Schema(
            name = "Response Message"
    )
    private String responseMessage;
    @Schema(
            name = "User Account Information Object"
    )
    private AccountInfo accountInfo;
}
