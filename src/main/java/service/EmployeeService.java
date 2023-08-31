package service;

import domain.Employee;

import java.util.List;

public interface EmployeeService {

    Employee getEmployeeById(int id);

    List<Employee> getEmployeesByMinSalary(int salary);

    List<Employee> getAllEmployees();

    boolean addEmployee(Employee employee);

    void insertEmployee(Employee employee);

    boolean updateEmployee(Employee employee);

    boolean deleteEmployee(Employee employee);

    int deleteEmployeesStartingFromId(int fromId);

    int batchUpdate(List <Employee> employees);

    int batchUpdate(List <Employee> employees, int batchSize);

}
