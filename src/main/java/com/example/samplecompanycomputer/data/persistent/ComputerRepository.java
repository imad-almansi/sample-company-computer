package com.example.samplecompanycomputer.data.persistent;

import com.example.samplecompanycomputer.model.Computer;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ComputerRepository extends JpaRepository<Computer, String> {
    List<Computer> findAllByEmployee_EmployeeAbbreviation(String employee);

    @Transactional
    Long deleteByMacAndEmployee_EmployeeAbbreviation(String mac, String employee);
}
