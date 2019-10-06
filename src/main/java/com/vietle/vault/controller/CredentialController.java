package com.vietle.vault.controller;

import com.vietle.vault.domain.Credential;
import com.vietle.vault.domain.ServiceRequest;
import com.vietle.vault.domain.ServiceResponse;
import com.vietle.vault.exception.CredentialException;
import com.vietle.vault.service.ICredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class CredentialController {
    private static Logger LOG = LoggerFactory.getLogger(CredentialController.class);
    private ICredential credential;

    @Autowired
    public CredentialController(ICredential credential) {
        this.credential = credential;
    }

    @GetMapping(value = "/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping(value = "/getAllCredential")
    public ServiceResponse getAllCredential() {
        ServiceResponse serviceResponse;
        try {
            serviceResponse =  this.credential.getAllCredential();
        } catch (CredentialException ce) {
            LOG.error(ce.getMessage());
            serviceResponse = ServiceResponse.builder().isServiceSuccess(false).isOperationSuccess(false).message(ce.getMessage()).build();
        }
        return serviceResponse;
    }

    @PostMapping(value = "/addCredential")
    public ServiceResponse addCredential(@RequestBody ServiceRequest request) {
        ServiceResponse serviceResponse;
        try {
            serviceResponse = this.credential.addCredential(request.getCredentials());
        } catch (CredentialException ce) {
            LOG.error(ce.getMessage());
            serviceResponse = ServiceResponse.builder().isServiceSuccess(false).isOperationSuccess(false).message(ce.getMessage()).build();
        }
        return serviceResponse;
    }

    @GetMapping(value = "/getCredential")
    public ServiceResponse getCredential(@RequestParam("query") String queryString) {
        ServiceResponse serviceResponse;
        try {
            serviceResponse = this.credential.getCredential(queryString);
        } catch (CredentialException ce) {
            LOG.error(ce.getMessage());
            serviceResponse = ServiceResponse.builder().isServiceSuccess(false).isOperationSuccess(false).message(ce.getMessage()).build();
        }
        return serviceResponse;
    }

    @DeleteMapping(value = "/deleteCredential/{id}")
    public ServiceResponse deleteCredential(@PathVariable("id") int id) {
        ServiceResponse serviceResponse;
        try {
            serviceResponse = this.credential.deleteCredential(id);
        } catch (CredentialException ce) {
            LOG.error(ce.getMessage());
            serviceResponse = ServiceResponse.builder().isServiceSuccess(false).isOperationSuccess(false).message(ce.getMessage()).build();
        }
        return serviceResponse;
    }

    @PatchMapping(value = "/updateCredential/{id}")
    public ServiceResponse updateCredential(@RequestBody Credential credential, @PathVariable int id) {

        return null;
    }
}
