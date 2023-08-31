import configuration.AppConfig;
import domain.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import service.EmployeeService;
import service.ServiceHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpringRunner {
    private static final Logger LOG = LoggerFactory.getLogger(SpringRunner.class);

    public static void main(String[] args) {
        LOG.trace("Запуск приложения");
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
//        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ServiceHelper serviceHelper = (ServiceHelper) context.getBean("serviceHelper");
        useEmployeeService(serviceHelper.getEmployeeService());
//        deleteNeedlessEmployees(serviceHelper.getEmployeeService(), 10);
        LOG.trace("Выход из приложения");
    }

    private static void useEmployeeService(EmployeeService employeeService) {
        int id = 1;
        System.out.println(String.format("Сотрудник с id = %d:", id));
        System.out.println(employeeService.getEmployeeById(id));

        System.out.println("Список всех сотрудников:");
        for (Employee employee : employeeService.getAllEmployees()) {
            System.out.println(employee);
        }

        int minSalary = 40000;
        System.out.println(String.format("Список сотрудников, у которых зарплата больше или равна %d:", minSalary));
        for (Employee employee : employeeService.getEmployeesByMinSalary(minSalary)) {
            System.out.println(employee);
        }

        Employee employee = new Employee();
        employee.setName("Пузиков Петр Иванович");
        employee.setOccupation("Программер");
        employee.setSalary(130000);
        employee.setAge(53);
        employee.setJoinDate(new Date(70, 6, 12));
        String insertResult = (employeeService.addEmployee(employee)) ? "Добавлен" : "Не добавлен";
        System.out.println(String.format("%s новый сотрудник: %s", insertResult, employee));

        employee = new Employee();
        employee.setName("Хреновщиков Валентин Астралович");
        employee.setOccupation("Аналитик");
        employee.setSalary(150000);
        employee.setAge(49);
        employee.setJoinDate(new Date(74, 2, 24));
        employeeService.insertEmployee(employee);
        System.out.println(String.format("Добавлен новый сотрудник: %s", employee));

        employeeService.insertEmployee(employee);
        employee.setName("Генномодифицированный Иван Степаныч");
        employee.setOccupation("Завхоз");
        employee.setSalary(50000);
        employee.setAge(59);
        employee.setJoinDate(new Date());
        String updateResult = (employeeService.updateEmployee(employee)) ? "Изменен" : "Не изменен";
        System.out.println(String.format("%s сотрудник: %s", updateResult, employee));

        employeeService.insertEmployee(employee);
        String deleteResult = (employeeService.deleteEmployee(employee)) ? "Удален" : "Не удален";
        System.out.println(String.format("%s сотрудник: %s", deleteResult, employee));

        int n = 50;
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Employee newEmployee = new Employee();
            newEmployee.setName("Пакетный Григорий Иваныч");
            newEmployee.setOccupation("Водитель");
            newEmployee.setSalary(60000);
            newEmployee.setAge(59);
            newEmployee.setJoinDate(new Date());
            employees.add(newEmployee);
        }
        System.out.println(String.format("Добавлено сотрудников пакетно: %s", employeeService.batchUpdate(employees)));

        int batchSize = 10;
        System.out.println(String.format("Добавлено сотрудников пакетно с размером пачки %d: %s", batchSize, employeeService.batchUpdate(employees, batchSize)));
    }

    private static void deleteNeedlessEmployees(EmployeeService employeeService, int fromId) {
        System.out.println(String.format("Удаляются сотрудники с id, большим или равным %d", fromId));
        System.out.println(String.format("Удалено сотрудников: %d", employeeService.deleteEmployeesStartingFromId(fromId)));
    }
}
