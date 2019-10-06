package com.vietle.vault.exception;

import lombok.Builder;

@Builder
public class CredentialException extends Exception{
    private int statusCode;
    private String message;

    public CredentialException(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
