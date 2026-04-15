package com.tenco.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString (exclude = "password")
public class Admin {
    private int id;
    private String userId;
    private String password;
    private String name;
    private LocalDateTime createdAt;
    private boolean isAvailable;
}