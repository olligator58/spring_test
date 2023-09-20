package service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ServiceHelper {
    @Resource
    private EmployeeService employeeService;
    @Resource
    private EmployeeXmlService employeeXmlService;
    @Resource
    private EmployeeHibernateService employeeHibernateService;

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public EmployeeXmlService getEmployeeXmlService() {
        return employeeXmlService;
    }

    public EmployeeHibernateService getEmployeeHibernateService() {
        return employeeHibernateService;
    }
}
