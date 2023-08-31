package service;

import dao.EmployeeDao;
import domain.Employee;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
    @Resource
    private EmployeeDao employeeDao;

    @Override
    public Employee getEmployeeById(int id) {
        return employeeDao.getEmployeeById(id);
    }

    @Override
    public List<Employee> getEmployeesByMinSalary(int salary) {
        return employeeDao.getEmployeesByMinSalary(salary);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeDao.getAllEmployees();
    }

    @Override
    public boolean addEmployee(Employee employee) {
        return employeeDao.addEmployee(employee);
    }

    @Override
    public void insertEmployee(Employee employee) {
        employeeDao.insertEmployee(employee);
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        return employeeDao.updateEmployee(employee);
    }

    @Override
    public boolean deleteEmployee(Employee employee) {
        return employeeDao.deleteEmployee(employee);
    }

    @Override
    public int deleteEmployeesStartingFromId(int fromId) {
        return employeeDao.deleteEmployeesStartingFromId(fromId);
    }

    @Override
    public int batchUpdate(List<Employee> employees) {
        return employeeDao.batchUpdate(employees);
//        return employeeDao.batchUpdate1(employees);
//        return employeeDao.batchUpdate2(employees);
    }

    @Override
    public int batchUpdate(List<Employee> employees, int batchSize) {
        return employeeDao.batchUpdate(employees, batchSize);
    }
}
