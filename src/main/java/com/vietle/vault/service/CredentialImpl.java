package com.vietle.vault.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietle.vault.domain.Credential;
import com.vietle.vault.domain.ServiceResponse;
import com.vietle.vault.exception.CredentialException;
import com.vietle.vault.util.CryptoUtil;
import com.vietle.vault.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CredentialImpl implements ICredential {
    private static Logger LOG = LoggerFactory.getLogger(CredentialImpl.class);

    private String fileLocation;
    private String credentialEncKey;
    private ObjectMapper objectMapper;

    @Autowired
    public CredentialImpl(@Value("${filelocation}") String fileLocation, @Value("${credentialEncKey}") String credentialEncKey, ObjectMapper objectMapper) {
        this.fileLocation = fileLocation;
        this.credentialEncKey = credentialEncKey;
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceResponse getAllCredential() throws CredentialException{
        Path path = Paths.get(fileLocation);
        ServiceResponse serviceResponse;
        try {
            String content = StringUtils.isEmpty(new String(Files.readAllBytes(path))) ? "{}" : new String(Files.readAllBytes(path));
            List<Credential> credentials = Arrays.asList(objectMapper.readValue(content, Credential[].class));
            credentials.stream().forEach(c->{
                String decryptedText = CryptoUtil.decryptString(c.getPassword(), credentialEncKey);
                c.setPassword(decryptedText);
            });
            serviceResponse = ServiceResponse.builder().message("success").isOperationSuccess(true)
                    .isServiceSuccess(true).credentials(credentials).build();
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
            throw new CredentialException(101, "error writing to file");
        }
        return serviceResponse;
    }

    @Override
    public ServiceResponse getCredential(String searchString) throws CredentialException{
        ServiceResponse serviceResponse;
        Path path = Paths.get(fileLocation);
        try {
            String content = new String(Files.readAllBytes(path));
            List<Credential> currentCredentials = Arrays.asList(objectMapper.readValue(content, Credential[].class));
            List<Credential> filteredResult =  currentCredentials.stream().filter(l->l.getName().contains(searchString)).collect(Collectors.toList());
            filteredResult.stream().forEach(c->{
                String decryptedText = CryptoUtil.decryptString(c.getPassword(), credentialEncKey);
                c.setPassword(decryptedText);
            });
            filteredResult.sort(Comparator.comparing(Credential::getId));
            serviceResponse = ServiceResponse.builder().isServiceSuccess(true).isOperationSuccess(true)
                    .credentials(filteredResult).message("success").build();
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
            throw new CredentialException(101, "error while querying credential" + ioe.getMessage());
        }
        return serviceResponse;
    }

    @Override
    public ServiceResponse addCredential(List<Credential> credentials) throws CredentialException {
        ServiceResponse serviceResponse;
        BufferedWriter writer;
        Path path = Paths.get(fileLocation);
        try {
            credentials.stream().forEach(c->{
                String encryptedString = CryptoUtil.encryptString(c.getPassword(), credentialEncKey);
                c.setPassword(encryptedString);
            });
            String content = new String(Files.readAllBytes(path));
            writer = ServiceUtil.bufferedWriter(fileLocation);
            if(StringUtils.isEmpty(content)) {
                credentials.sort(Comparator.comparing(Credential::getId));
                writer.write(objectMapper.writeValueAsString(credentials));
            } else {
                List<Credential> currentCredentials = Arrays.asList(objectMapper.readValue(content, Credential[].class));
                List<Credential> combinedCredentials = Stream.concat(credentials.stream(), currentCredentials.stream()).collect(Collectors.toList());
                combinedCredentials.sort(Comparator.comparing(Credential::getId));
                writer.write(objectMapper.writeValueAsString(combinedCredentials));
            }
            writer.close();
            serviceResponse = ServiceResponse.builder().isServiceSuccess(true).isOperationSuccess(true)
                    .credentials(credentials).message("success").build();
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
            throw new CredentialException(101, "error while adding credential" + ioe.getMessage());
        }
        return serviceResponse;
    }

    @Override
    public ServiceResponse deleteCredential(int id) throws CredentialException {
        ServiceResponse serviceResponse;
        BufferedWriter writer;
        Path path = Paths.get(fileLocation);
        try {
            String content = new String(Files.readAllBytes(path));
            List<Credential> currentCredentials = new ArrayList<>(Arrays.asList(objectMapper.readValue(content, Credential[].class)));
            currentCredentials.removeIf(c->c.getId() == id);
            writer = ServiceUtil.bufferedWriter(fileLocation);
            writer.write(objectMapper.writeValueAsString(currentCredentials));
            writer.close();
            serviceResponse = ServiceResponse.builder().isServiceSuccess(true).isOperationSuccess(true)
                    .credentials(currentCredentials).message("success").build();
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
            throw new CredentialException(101, "error while deleting credential" + ioe.getMessage());
        }
        return serviceResponse;
    }

    @Override
    public ServiceResponse updateCredential(Credential credential, int id) throws CredentialException {
        ServiceResponse serviceResponse;
        BufferedWriter writer;
        Path path = Paths.get(fileLocation);
        try {
            String content = new String(Files.readAllBytes(path));
            List<Credential> currentCredentials = new ArrayList<>(Arrays.asList(objectMapper.readValue(content, Credential[].class)));
            Optional<Credential> opt = currentCredentials.stream().filter(c->c.getId() == id).findAny();
            if(opt.isPresent()) {
                Credential credentialToUpdate = opt.get();
                if(!StringUtils.isEmpty(credential.getPassword())) {
                    String encryptedPassword = CryptoUtil.encryptString(credential.getPassword(), credentialEncKey);
                    credentialToUpdate.setPassword(encryptedPassword);
                }
                if(!StringUtils.isEmpty(credential.getName())) {
                    credentialToUpdate.setName(credential.getName());
                }
                if(!StringUtils.isEmpty(credential.getCategory())) {
                    credentialToUpdate.setCategory(credential.getCategory());
                }
                if(!StringUtils.isEmpty(credential.getText())) {
                    credentialToUpdate.setText(credential.getText());
                }
                if(!StringUtils.isEmpty(credential.getUsername())) {
                    credentialToUpdate.setUsername(credential.getUsername());
                }
                int indexOfCredentialToUpdate = currentCredentials.indexOf(credentialToUpdate);
                currentCredentials.set(indexOfCredentialToUpdate, credentialToUpdate);
                writer = ServiceUtil.bufferedWriter(fileLocation);
                writer.write(objectMapper.writeValueAsString(currentCredentials));
                writer.close();
                List<Credential> updatedItem = new ArrayList<>(1);
                updatedItem.add(credential);
                serviceResponse =  ServiceResponse.builder().isServiceSuccess(true).isOperationSuccess(true)
                        .credentials(updatedItem).message("success").build();
            } else {
                serviceResponse = ServiceResponse.builder().isServiceSuccess(false).isOperationSuccess(true)
                        .credentials(null).message("not able to find credential").build();
            }
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
            throw new CredentialException(101, "error while deleting credential" + ioe.getMessage());
        }
        return serviceResponse;
    }
}
