package dao;

import domain.Employee;
import domain.Task;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EmployeeHibernateDao {
    private SessionFactory sessionFactory;
    private HibernateTemplate hibernateTemplate;
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeHibernateDao.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Transactional(transactionManager = "txManager", readOnly = true)
    public Employee getEmployeeById(int id, boolean fetchTasks) {
        Employee employee = hibernateTemplate.get(Employee.class, id);
        if (employee != null) {
            loadTasks(employee, fetchTasks);
        } else {
            LOG.info("Сотрудник с id = {} не найден", id);
        }
        return employee;
    }

    @Transactional(transactionManager = "txManager", readOnly = true)
    public List<Employee> getEmployeesByMinSalary(int salary, boolean fetchTasks) {
        String hql = "from Employee where salary >= :salary";
        LOG.info("Выполняется запрос {}, salary = {}", hql, salary);
        List<Employee> employees = sessionFactory.getCurrentSession()
                .createQuery(hql, Employee.class)
                .setParameter("salary", salary)
                .list();
        for (Employee employee : employees) {
            loadTasks(employee, fetchTasks);
        }
        return employees;
    }

    @Transactional(transactionManager = "txManager", readOnly = true)
    public List<Employee> getAllEmployees(boolean fetchTasks) {
        String hql = "from Employee";
        LOG.info("Выполняется запрос {}", hql);
        List<Employee> employees = sessionFactory.getCurrentSession()
                .createQuery(hql, Employee.class)
                .list();
        for (Employee employee : employees) {
            loadTasks(employee, fetchTasks);
        }
        return employees;
    }

    @Transactional(transactionManager = "txManager")
    public void saveEmployee(Employee employee) {
        hibernateTemplate.persist(employee);
        LOG.info("Добавлен сотрудник с id = {}", employee.getId());
        for (Task task : employee.getTasks()) {
            LOG.info("Добавлена задача с id = {}", task.getId());
        }
    }

    @Transactional(transactionManager = "txManager")
    public void saveEmployees(List<Employee> employees) {
        for (Employee employee : employees) {
            saveEmployee(employee);
        }
    }

    @Transactional(transactionManager = "txManager")
    public void updateEmployee(Employee employee) {
        hibernateTemplate.update(employee);
        LOG.info("Изменен сотрудник с id = {}", employee.getId());
    }

    @Transactional(transactionManager = "txManager")
    public void updateEmployees(List<Employee> employees) {
        for (Employee employee : employees) {
            updateEmployee(employee);
        }
    }

    @Transactional(transactionManager = "txManager")
    public void deleteEmployee(Employee employee) {
        hibernateTemplate.delete(employee);
        LOG.info("Удален сотрудник с id = {}", employee.getId());
        for (Task task : employee.getTasks()) {
            LOG.info("Удалена задача с id = {}", task.getId());
        }
    }

    @Transactional(transactionManager = "txManager")
    public void deleteEmployees(List<Employee> employees) {
        for (Employee employee : employees) {
            deleteEmployee(employee);
        }
    }

    @Transactional(transactionManager = "txManager")
    public void deleteEmployeesAndTasksStartingFromId(int fromId) {
        deleteEmployees(getEmployeesAndTasksStartingFromId(fromId));
    }

    @Transactional
    List<Employee> getEmployeesAndTasksStartingFromId(int fromId) {
        String hql = "from Employee where id >= :id";
        LOG.info("Выполняется запрос {}", hql);
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Employee.class)
                .setParameter("id", fromId)
                .list();
    }

    private void loadTasks(Employee employee, boolean fetchTasks) {
        if (fetchTasks) {
            Hibernate.initialize(employee.getTasks());
        } else {
            // чтобы при просмотре задач сотрудника не выскакивала ошибка LazyInitializationException
            employee.setTasks(new ArrayList<>());
        }
    }


}
