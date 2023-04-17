package com.example.samplecompanycomputer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Employee implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @Pattern(regexp = "^[a-z]{3}$", message = "Employee must be 3 latin letters")
    private String employeeAbbreviation;

    public Employee() {

    }
}
