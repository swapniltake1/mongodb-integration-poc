package com.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class DemoData {
    @Id
    private String id;
    private User user;
    private Payment payment;

    // Getters and setters
}