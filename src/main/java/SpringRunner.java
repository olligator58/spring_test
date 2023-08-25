import domain.Employee;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.ServiceHelper;

public class SpringRunner {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        ServiceHelper serviceHelper = (ServiceHelper) context.getBean("serviceHelper");
        System.out.println(serviceHelper.getEmployeeService().getEmployeeById(1));
        for (Employee employee : serviceHelper.getEmployeeService().getAllEmployees()) {
            System.out.println(employee);
        }

        for (Employee employee : serviceHelper.getEmployeeService().getEmployeesByMinSalary(40000)) {
            System.out.println(employee);
        }
    }
}
