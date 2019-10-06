package com.vietle.vault.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {
    private boolean isServiceSuccess;
    private boolean isOperationSuccess;
    private String message;
    private List<Credential> credentials;
}
