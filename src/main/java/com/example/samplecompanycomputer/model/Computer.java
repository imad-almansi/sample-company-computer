package com.example.samplecompanycomputer.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
public class Computer implements Serializable {
    @Id
    @NotBlank(message = "MAC Address is required")
    @Pattern(regexp = "^[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}$",
            message = "Invalid MAC address")
    private String mac;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "IP Address is required")
    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
            message = "Invalid IP4 address")
    private String ip;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "EMPLOYEE_ABBREVIATION", referencedColumnName = "employeeAbbreviation")
    private Employee employee;
    private String description;

    protected Computer() {

    }
}
