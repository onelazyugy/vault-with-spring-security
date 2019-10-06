package com.vietle.vault.domain;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credential{
    private int id;
    private String name;
    private String category;
    private String text;
    private String username;
    private String password;
}
