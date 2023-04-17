package com.example.samplecompanycomputer;

import com.example.samplecompanycomputer.data.persistent.ComputerRepository;
import com.example.samplecompanycomputer.data.persistent.EmployeeRepository;
import com.example.samplecompanycomputer.model.Computer;
import com.example.samplecompanycomputer.model.Employee;
import com.example.samplecompanycomputer.model.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class SampleCompanyComputerController {

    @Autowired
    private ComputerRepository computerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    private HttpClient httpClient;

    @Value("${notify.url}")
    private String notifyUrl;

    @PostMapping("/add")
    public Computer add(@RequestParam(value = "name") String name,
                        @RequestParam(value = "mac") String mac,
                        @RequestParam(value = "ip") String ip,
                        @RequestParam(value = "employee", required = false) String employee,
                        @RequestParam(value = "description", required = false) String description) throws JsonProcessingException {

        Computer computer = new Computer(mac, name, ip, null, description);
        if (StringUtils.hasText(employee)) {
            Optional<Employee> employeeResult = employeeRepository.findByEmployeeAbbreviation(employee);
            employeeResult.ifPresentOrElse(computer::setEmployee, () -> {
                Employee employeeObj = new Employee(null, employee);
                computer.setEmployee(employeeObj);
            });
        }
        Computer result = computerRepository.save(computer);

        checkComputersPerEmployee(employee);

        return result;
    }

    @GetMapping("/")
    public List<Computer> get(@RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "mac", required = false) String mac,
                              @RequestParam(value = "employee", required = false) String employee) {

        if (StringUtils.hasText(mac)) {
            List<Computer> result = new ArrayList<>();
            computerRepository.findById(mac).ifPresent(result::add);
            return result;
        }

        return computerRepository.findAll().stream().filter(computer -> {
            boolean nameMatch = true;
            boolean employeeMatch = true;
            if (StringUtils.hasText(name)) {
                nameMatch = computer.getName().equals(name);
            }
            if (StringUtils.hasText(employee)) {
                Employee employeeObj = computer.getEmployee();
                if (null != employeeObj) {
                    employeeMatch = employeeObj.getEmployeeAbbreviation().equals(employee);
                } else {
                    employeeMatch = false;
                }
            }
            return nameMatch && employeeMatch;
        }).collect(Collectors.toList());
    }

    @PostMapping("/assign")
    public Computer add(@RequestParam(value = "mac") String mac,
                        @RequestParam(value = "employee") String employee) throws HttpClientErrorException, JsonProcessingException {

        var ref = new Object() {
            Computer result;
        };

        computerRepository.findById(mac).ifPresentOrElse(computer1 -> {
            Optional<Employee> employeeResult = employeeRepository.findByEmployeeAbbreviation(employee);
            employeeResult.ifPresentOrElse(employee1 -> {
                computer1.setEmployee(employee1);
                ref.result = computerRepository.save(computer1);
            }, () -> {
                Employee employeeObj = new Employee(null, employee);
                computer1.setEmployee(employeeObj);
                ref.result = computerRepository.save(computer1);
            });
        }, () -> ref.result = null);

        if (null == ref.result) {
            throw new HttpClientErrorException("Computer does not exist", HttpStatus.CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), null, null, null);
        }

        checkComputersPerEmployee(employee);

        return ref.result;
    }

    @DeleteMapping("/delete")
    public Long delete(@RequestParam(value = "mac") String mac,
                       @RequestParam(value = "employee") String employee) {

        return computerRepository.deleteByMacAndEmployee_EmployeeAbbreviation(mac, employee);
    }

    private void checkComputersPerEmployee(String employee) throws JsonProcessingException {
        if (StringUtils.hasText(employee)) {
            List<Computer> computers = computerRepository.findAllByEmployee_EmployeeAbbreviation(employee);
            if (computers.size() >= 3) {
                Notification warning = new Notification("warning", employee, "employee has more than 3 computers");
                sendNotification(warning);

            }
        }
    }

    private void sendNotification(Notification notification) throws JsonProcessingException {
        if (null == httpClient) {
            httpClient = HttpClient.newHttpClient();
        }

        URI notifyUri = URI.create(notifyUrl);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter().withDefaultPrettyPrinter();
        String notificationJson = ow.writeValueAsString(notification);
        HttpRequest httpRequest = HttpRequest.newBuilder(notifyUri).
                POST(HttpRequest.BodyPublishers.ofString(notificationJson)).
                header("Content-Type", "application/json").
                build();

        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
}