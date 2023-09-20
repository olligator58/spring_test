import configuration.AppConfig;
import domain.Employee;
import domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.EmployeeHibernateService;
import service.EmployeeService;
import service.EmployeeXmlService;
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
//        useEmployeeService(serviceHelper.getEmployeeService());
//        deleteNeedlessEmployees(serviceHelper.getEmployeeService(), 10);
//        useEmployeeHibernateService(serviceHelper.getEmployeeHibernateService());
        useEmployeeXmlService(serviceHelper);
//        deleteNeedlessEmployeesAndTasks(serviceHelper.getEmployeeHibernateService(), 10);
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

    private static void deleteNeedlessEmployeesAndTasks(EmployeeHibernateService employeeService, int fromId) {
        System.out.println(String.format("Удаляются сотрудники с id, большим или равным %d, и их задачи", fromId));
        employeeService.deleteEmployeesAndTasksStartingFromId(fromId);
    }

    private static void useEmployeeXmlService(ServiceHelper serviceHelper) {
        EmployeeHibernateService employeeService = serviceHelper.getEmployeeHibernateService();
        EmployeeXmlService employeeXmlService = serviceHelper.getEmployeeXmlService();

        System.out.println("Сохраняем сотрудников из базы в xml-файле...");
        employeeXmlService.saveEmployees(employeeService.getAllEmployees(true));
        String message = "Список сотрудников, загруженных из xml-файла:";
        printEmployees(employeeXmlService.loadEmployees(), message);
        /*System.out.println("Список сотрудников, загруженных из xml-файла:");
        for (Employee employee : employeeXmlService.loadEmployees()) {
            System.out.println(employee);
        }*/
    }

    private static void useEmployeeHibernateService(EmployeeHibernateService employeeService) {
        int id = 1;
        printEmployee(employeeService.getEmployeeById(id, false), id);

        id = 5;
        printEmployee(employeeService.getEmployeeById(id, true), id);

        int salary = 40000;
        String message = String.format("Сотрудники с зарплатой >= %d:", salary);
        printEmployees(employeeService.getEmployeesByMinSalary(salary, false), message);

        salary = 60000;
        message = String.format("Сотрудники с зарплатой >= %d:", salary);
        printEmployees(employeeService.getEmployeesByMinSalary(salary, true), message);

        message = "Список всех сотрудников (без задач):";
        printEmployees(employeeService.getAllEmployees(false), message);

        message = "Список всех сотрудников (с задачами):";
        printEmployees(employeeService.getAllEmployees(true), message);

        Employee employee = new Employee();
        employee.setName("Хиберов Анатолий Зосимович");
        employee.setOccupation("Фронт программер");
        employee.setSalary(150000);
        employee.setAge(34);
        employee.setJoinDate(new Date(120, 5, 20));
        employeeService.saveEmployee(employee);
        printEmployee(employee, employee.getId());

        List<Employee> employees = generateEmployeesList(10, "Зайцев Фома Ильич", "Бэк программер");
        employeeService.saveEmployees(employees);
        message = "Список добавленных сотрудников:";
        printEmployees(employees, message);

        employee = new Employee();
        employee.setName("Неизвестный");
        employee.setOccupation("не пойми кто");
        employee.setSalary(0);
        employee.setAge(0);
        employee.setJoinDate(new Date(0, 0, 0));
        employeeService.saveEmployee(employee);
        printEmployee(employee, employee.getId());
        employee.setName("Забайдачный Марат Иванович");
        employee.setOccupation("Завхоз");
        employee.setSalary(50000);
        employee.setAge(53);
        employee.setJoinDate(new Date(119, 2, 12));
        employeeService.updateEmployee(employee);
        printEmployee(employee, employee.getId());

        employees = generateEmployeesList(10, "Коровьев Глеб Иваныч", "Свинопас");
        employeeService.saveEmployees(employees);
        message = "Эти сотрудники будут изменены:";
        printEmployees(employees, message);
        for (Employee e : employees) {
            e.setName("Огурцов Семен Палыч");
            e.setSalary(20000);
        }
        employeeService.updateEmployees(employees);
        message = "Список измененных сотрудников:";
        printEmployees(employees, message);

        employee = employees.get(0);
        employees.remove(0);
        System.out.println("Этот сотрудник будет удален:");
        printEmployee(employee, employee.getId());
        employeeService.deleteEmployee(employee);

        for (int i = 0; i < 5; i++) {
            employees.remove(employees.size() - 1);
        }
        message = "Эти сотрудники будут удалены:";
        printEmployees(employees, message);
        employeeService.deleteEmployees(employees);

        employees = generateEmployeesList(5, "Голиков Илья Петрович", "Водитель");
        for (Employee e : employees) {
            for (Task task : generateTasksList(3, "Отвезти начальника")) {
                e.addTask(task);
            }
        }
        employeeService.saveEmployees(employees);
        message = "Добавлены следующие сотрудники и задачи:";
        printEmployees(employees, message);

        /*int fromId = 10;
        employeeService.deleteEmployeesAndTasksStartingFromId(fromId);*/
    }

    private static void printEmployee(Employee employee, int id) {
        System.out.println(String.format("Сотрудник с id = %d:", id));
        System.out.println(employee);
        List<Task> tasks = (employee != null) ? employee.getTasks() : new ArrayList<>();
        System.out.println(String.format("Задачи сотрудника с id = %d:", id));
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private static void printEmployees(List<Employee> employees, String message) {
        System.out.println(message);
        for (Employee e : employees) {
            System.out.println(e);
            System.out.println(String.format("Задачи сотрудника с id = %d:", e.getId()));
            for (Task task : e.getTasks()) {
                System.out.println(task);
            }
        }
    }

    private static List<Employee> generateEmployeesList(int number, String name, String occupation) {
        List<Employee> employees = new ArrayList<>();
        int salary = 150000;
        int age = 25;
        for (int i = 0; i < number; i++) {
            Employee employee = new Employee();
            employee.setName(name + " #" + (i + 1));
            employee.setOccupation(occupation + " #" + (i + 1));
            employee.setSalary(salary);
            employee.setAge(age);
            employee.setJoinDate(new Date());
            employees.add(employee);
            salary += 1000;
            age += 1;
        }
        return employees;
    }

    private static List<Task> generateTasksList(int number, String name) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Task task = new Task();
            task.setName(name + " #" + (i + 1));
            task.setDeadline(new Date());
            tasks.add(task);
        }
        return tasks;
    }
}
