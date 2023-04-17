package com.example.samplecompanycomputer.data.persistent;

import com.example.samplecompanycomputer.model.Employee;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void whenCalledFindByEmployeeAbbreviation_thenEmployeeFound() {
        Employee employeeA = new Employee(1L, "tsa");
        Employee employeeB = new Employee(2L, "tsb");

        employeeRepository.save(employeeA);
        employeeRepository.save(employeeB);

        Long numOfEmployees = employeeRepository.count();
        assertEquals(2, numOfEmployees);

        Optional<Employee> employee1 = employeeRepository.findByEmployeeAbbreviation(employeeA.getEmployeeAbbreviation());
        Optional<Employee> employee2 = employeeRepository.findByEmployeeAbbreviation(employeeB.getEmployeeAbbreviation());
        Optional<Employee> employee3 = employeeRepository.findByEmployeeAbbreviation("xxx");

        assertTrue(employee1.isPresent());
        assertEquals(employee1.get().toString(), employeeA.toString());
        assertTrue(employee2.isPresent());
        assertEquals(employee2.get().toString(), employeeB.toString());
        assertTrue(employee3.isEmpty());
    }
}