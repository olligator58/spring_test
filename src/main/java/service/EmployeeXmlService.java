package service;

import domain.Employee;
import domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class EmployeeXmlService {
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    @Resource
    private Environment environment;
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeXmlService.class);

    @Autowired
    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    @Autowired
    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
        ((XStreamMarshaller) this.unmarshaller).getXStream().allowTypes(new Class[]{Employee.class, Task.class});
    }

    public void saveEmployees(List<Employee> employees) {
        LOG.info("Имя файла для выгрузки: {}", getFileName());
        try (FileOutputStream fos = new FileOutputStream(getFileName())) {
            marshaller.marshal(employees, new StreamResult(fos));
            LOG.info("Выгрузка списка сотрудников в файл {} прошла успешно", getFileName());
        } catch (Exception e) {
            LOG.info("Выгрузка списка сотрудников в файл {} не удалась", getFileName());
            LOG.trace(e.getMessage(), e);
        }
    }

    public List<Employee> loadEmployees() {
        LOG.info("Имя файла для загрузки: {}", getFileName());
        try (FileInputStream fis = new FileInputStream(getFileName())) {
            return (List<Employee>) unmarshaller.unmarshal(new StreamSource(fis));
        } catch (Exception e) {
            LOG.info("Загрузка списка сотрудников из файла {} не удалась", getFileName());
            LOG.trace(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    private String getFileName() {
        return environment.getProperty("employee.xmlFileName");
    }
}
