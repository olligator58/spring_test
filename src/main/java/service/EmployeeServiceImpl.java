package service;

import dao.EmployeeDao;
import domain.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Resource
    private EmployeeDao employeeDao;

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(int id) {
        return employeeDao.getEmployeeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByMinSalary(int salary) {
        return employeeDao.getEmployeesByMinSalary(salary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeDao.getAllEmployees();
    }

    @Override
    @Transactional
    public boolean addEmployee(Employee employee) {
        return employeeDao.addEmployee(employee);
    }

    @Override
    @Transactional
    public void insertEmployee(Employee employee) {
        employeeDao.insertEmployee(employee);
    }

    @Override
    @Transactional
    public boolean updateEmployee(Employee employee) {
        return employeeDao.updateEmployee(employee);
    }

    @Override
    @Transactional
    public boolean deleteEmployee(Employee employee) {
        return employeeDao.deleteEmployee(employee);
    }

    @Override
    @Transactional
    public int deleteEmployeesStartingFromId(int fromId) {
        return employeeDao.deleteEmployeesStartingFromId(fromId);
    }

    @Override
    @Transactional
    public int batchUpdate(List<Employee> employees) {
        return employeeDao.batchUpdate(employees);
//        return employeeDao.batchUpdate1(employees);
//        return employeeDao.batchUpdate2(employees);
    }

    @Override
    @Transactional
    public int batchUpdate(List<Employee> employees, int batchSize) {
        return employeeDao.batchUpdate(employees, batchSize);
    }
}
