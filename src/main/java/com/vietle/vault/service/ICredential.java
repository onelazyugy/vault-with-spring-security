package com.vietle.vault.service;

import com.vietle.vault.domain.Credential;
import com.vietle.vault.domain.ServiceResponse;
import com.vietle.vault.exception.CredentialException;

import java.util.List;

public interface ICredential {
    ServiceResponse getAllCredential() throws CredentialException;
    ServiceResponse getCredential(String pattern) throws CredentialException;
    ServiceResponse addCredential(List<Credential> credentials) throws CredentialException;
    ServiceResponse updateCredential(Credential credentials, int id) throws CredentialException;
    ServiceResponse deleteCredential(int id) throws CredentialException;
}
