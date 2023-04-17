package com.example.samplecompanycomputer.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonSerialize
public class Notification {
    @NonNull
    private String level;
    @NonNull
    private String employeeAbbreviation;
    @NonNull
    private String message;
}
