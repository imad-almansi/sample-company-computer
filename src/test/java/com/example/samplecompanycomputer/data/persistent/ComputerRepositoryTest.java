package com.example.samplecompanycomputer.data.persistent;

import com.example.samplecompanycomputer.model.Computer;
import com.example.samplecompanycomputer.model.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ComputerRepositoryTest {
    @Autowired
    private ComputerRepository computerRepository;

    @Test
    public void whenCalledSave_thenCorrectNumberOfComputers() {
        computerRepository.save(new Computer("ab:cd:ef:gh:ij:kl", "pc","0.0.0.0", null, null));
        Long numOfComputers = computerRepository.count();

        assertEquals(1, numOfComputers);
    }

    @Test
    public void whenCalledFindAllByEmployee_EmployeeAbbreviation_thenComputerFound() {
        String mac1 = "ab:cd:ef:gh:ij:k1";
        String mac2 = "ab:cd:ef:gh:ij:k2";
        Employee employeeA = new Employee(1L, "tsa");
        Employee employeeB = new Employee(2L, "tsb");

        Computer computer1 = new Computer(mac1, "pc","0.0.0.0", employeeA, null);
        Computer computer2 = new Computer(mac2, "pc","0.0.0.0", employeeB, null);

        computerRepository.save(computer1);
        computerRepository.save(computer2);

        Long numOfComputers = computerRepository.count();
        assertEquals(2, numOfComputers);

        List<Computer> computers1 = computerRepository.findAllByEmployee_EmployeeAbbreviation(employeeA.getEmployeeAbbreviation());
        List<Computer> computers2 = computerRepository.findAllByEmployee_EmployeeAbbreviation(employeeB.getEmployeeAbbreviation());

        assertEquals(1, computers1.size());
        assertEquals(computer1.toString(), computers1.get(0).toString());
        assertEquals(1, computers2.size());
        assertEquals(computer2.toString(), computers2.get(0).toString());
    }

    @Test
    public void whenCalledDelete_thenComputerDeleted() {
        String mac = "ab:cd:ef:gh:ij:kl";
        computerRepository.save(new Computer(mac, "pc","0.0.0.0", null, null));
        Long numOfComputers = computerRepository.count();
        assertEquals(1, numOfComputers);

        computerRepository.deleteById(mac);

        numOfComputers = computerRepository.count();
        assertEquals(0, numOfComputers);
    }

    @Test
    public void whenCalledDeleteByMacAndEmployee_EmployeeAbbreviation_thenOnlyMatchingComputerDeleted() {
        String mac1 = "ab:cd:ef:gh:ij:k1";
        String mac2 = "ab:cd:ef:gh:ij:k2";
        Employee employeeA = new Employee(1L, "tsa");
        Employee employeeB = new Employee(2L, "tsb");

        Computer computer1 = new Computer(mac1, "pc","0.0.0.0", employeeA, null);
        Computer computer2 = new Computer(mac2, "pc","0.0.0.0", employeeB, null);

        computerRepository.save(computer1);
        computerRepository.save(computer2);

        Long numOfComputers = computerRepository.count();
        assertEquals(2, numOfComputers);

        computerRepository.deleteByMacAndEmployee_EmployeeAbbreviation(mac1, employeeB.getEmployeeAbbreviation());
        numOfComputers = computerRepository.count();
        assertEquals(2, numOfComputers);

        computerRepository.deleteByMacAndEmployee_EmployeeAbbreviation(mac1, employeeA.getEmployeeAbbreviation());
        numOfComputers = computerRepository.count();
        assertEquals(1, numOfComputers);
    }
}