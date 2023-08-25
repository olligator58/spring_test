package service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ServiceHelper {
    @Resource
    private EmployeeService employeeService;

    public EmployeeService getEmployeeService() {
        return employeeService;
    }
}
