package service;

import dao.EmployeeHibernateDao;
import domain.Employee;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EmployeeHibernateService {
    @Resource
    private EmployeeHibernateDao employeeHibernateDao;

    public Employee getEmployeeById(int id, boolean fetchTasks) {
        return employeeHibernateDao.getEmployeeById(id, fetchTasks);
    }

    public List<Employee> getEmployeesByMinSalary(int salary, boolean fetchTasks) {
        return employeeHibernateDao.getEmployeesByMinSalary(salary, fetchTasks);
    }

    public List<Employee> getAllEmployees(boolean fetchTasks) {
        return employeeHibernateDao.getAllEmployees(fetchTasks);
    }

    public void saveEmployee(Employee employee) {
        employeeHibernateDao.saveEmployee(employee);
    }

    public void saveEmployees(List<Employee> employees) {
        employeeHibernateDao.saveEmployees(employees);
    }

    public void updateEmployee(Employee employee) {
        employeeHibernateDao.updateEmployee(employee);
    }

    public void updateEmployees(List<Employee> employees) {
        employeeHibernateDao.updateEmployees(employees);
    }

    public void deleteEmployee(Employee employee) {
        employeeHibernateDao.deleteEmployee(employee);
    }

    public void deleteEmployees(List<Employee> employees) {
        employeeHibernateDao.deleteEmployees(employees);
    }

    public void deleteEmployeesAndTasksStartingFromId(int fromId) {
        employeeHibernateDao.deleteEmployeesAndTasksStartingFromId(fromId);
    }
}
