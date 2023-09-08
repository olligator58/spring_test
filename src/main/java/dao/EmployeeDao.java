package dao;

import domain.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class EmployeeDao {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SimpleJdbcInsert insertActor;
    private final EmployeeMapper employeeMapper = new EmployeeMapper();
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeDao.class);

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("employee")
                .usingGeneratedKeyColumns("id");
    }

    public Employee getEmployeeById(int id) {
        String sql = "select * from employee where id = ?";
        LOG.info("Выполняется запрос {}, id = {} ", sql, id);
        try {
            return jdbcTemplate.queryForObject(sql, employeeMapper, id);
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public List<Employee> getEmployeesByMinSalary(int salary) {
        String sql = "select * from employee where salary >= :salary";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("salary", salary);
        LOG.info("Выполняется запрос {}, params = {} ", sql, params);
        try {
            return namedParameterJdbcTemplate.query(sql, params, employeeMapper);
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Employee> getAllEmployees() {
        String sql = "select * from employee";
        LOG.info("Выполняется запрос {}", sql);
        try {
            return jdbcTemplate.query(sql, employeeMapper);
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public boolean addEmployee(Employee employee) {
        boolean result;
        String sql = "insert into employee (name, occupation, salary, age, join_date) values " +
                     "(:name, :occupation, :salary, :age, :joinDate)";
        SqlParameterSource params = getMapSqlParameterSource(employee);
        int oldId = getMaxId();
        LOG.info("Выполняется запрос {}, params = {} ", sql, params);
        try {
            result = namedParameterJdbcTemplate.update(sql, params) > 0;
            int newId = getMaxId();
            if (newId > oldId) {
                employee.setId(newId);
            }
            return result;
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public void insertEmployee(Employee employee) {
        SqlParameterSource params = getMapSqlParameterSource(employee);
        LOG.info("Выполняется запрос на вставку, params = {}", params);
        Number newId = insertActor.executeAndReturnKey(params);
        employee.setId(newId.intValue());
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "update employee set " +
                     "name = :name, " +
                     "occupation = :occupation, " +
                     "salary = :salary, " +
                     "age = :age, " +
                     "join_date = :joinDate " +
                     "where id = :id";
        MapSqlParameterSource params = getMapSqlParameterSource(employee);
        params.addValue("id", employee.getId());
        LOG.info("Выполняется запрос {}, params = {} ", sql, params);
        try {
            return namedParameterJdbcTemplate.update(sql, params) > 0;
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteEmployee(Employee employee) {
        String sql = "delete from employee where id = ?";
        LOG.info("Выполняется запрос {}, id = {}", sql, employee.getId());
        try {
            return jdbcTemplate.update(sql, employee.getId()) > 0;
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public int deleteEmployeesStartingFromId(int fromId) {
        String sql = "delete from employee where id >= ?";
        LOG.info("Выполняется запрос {}, fromId = {}", sql, fromId);
        try {
            return jdbcTemplate.update(sql, fromId);
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return 0;
        }
    }

    public int batchUpdate(List<Employee> employees) {
        setEmployeesIds(employees);
        String sql = "insert into employee (id, name, occupation, salary, age, join_date) values " +
                "(?, ?, ?, ?, ?, ?)";
        LOG.info("Выполняется пакетный запрос {}", sql);
        try {
            return Arrays.stream(jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    fillPreparedStatement(preparedStatement, employees.get(i));
                }

                @Override
                public int getBatchSize() {
                    return employees.size();
                }
            })).sum();
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return 0;
        }
    }

    public int batchUpdate1(List<Employee> employees) {
        setEmployeesIds(employees);
        String sql = "insert into employee (id, name, occupation, salary, age, join_date) values " +
                "(:id, :name, :occupation, :salary, :age, :joinDate)";
        LOG.info("Выполняется пакетный запрос {}", sql);
        try {
            return Arrays.stream(namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(employees)))
                    .sum();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return 0;
        }
    }

    public int batchUpdate2(List<Employee> employees) {
        setEmployeesIds(employees);
        String sql = "insert into employee (id, name, occupation, salary, age, join_date) values " +
                "(?, ?, ?, ?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (Employee employee : employees) {
            Object[] values = new Object[] {employee.getId(), employee.getName(), employee.getOccupation(),
                                            employee.getSalary(), employee.getAge(), employee.getJoinDate()};
            batch.add(values);
        }
        LOG.info("Выполняется пакетный запрос {}", sql);
        try {
            return Arrays.stream(jdbcTemplate.batchUpdate(sql, batch))
                    .sum();
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return 0;
        }
    }

    public int batchUpdate(List<Employee> employees, int batchSize) {
        setEmployeesIds(employees);
        String sql = "insert into employee (id, name, occupation, salary, age, join_date) values " +
                "(?, ?, ?, ?, ?, ?)";
        LOG.info("Выполняется пакетный запрос {}, batchSize = {}", sql, batchSize);
        try {
            return Arrays.stream(jdbcTemplate.batchUpdate(sql, employees, batchSize, new ParameterizedPreparedStatementSetter<Employee>() {
                        @Override
                        public void setValues(PreparedStatement preparedStatement, Employee employee) throws SQLException {
                            fillPreparedStatement(preparedStatement, employee);
                        }
                    })).parallel()
                    .flatMapToInt(Arrays::stream)
                    .sum();
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return 0;
        }
    }

    private Integer getMaxId() {
        String sql = "select max(id) from employee";
        LOG.info("Выполняется запрос {}", sql);
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return -1;
        }
    }

    private MapSqlParameterSource getMapSqlParameterSource(Employee employee) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", employee.getName());
        params.addValue("occupation", employee.getOccupation());
        params.addValue("salary", employee.getSalary());
        params.addValue("age", employee.getAge());
        params.addValue("joinDate", employee.getJoinDate());
        return params;
    }

    private void setEmployeesIds(List<Employee> employees ) {
        int newId = getMaxId() + 1;
        for (Employee employee : employees) {
            employee.setId(newId);
            newId++;
        }
    }

    private void fillPreparedStatement(PreparedStatement preparedStatement, Employee employee) throws SQLException {
        preparedStatement.setInt(1, employee.getId());
        preparedStatement.setString(2, employee.getName());
        preparedStatement.setString(3, employee.getOccupation());
        preparedStatement.setInt(4, employee.getSalary());
        preparedStatement.setInt(5, employee.getAge());
        preparedStatement.setObject(6, employee.getJoinDate());
    }

    private static class EmployeeMapper implements RowMapper<Employee> {
        @Override
        public Employee mapRow(ResultSet resultSet, int i) throws SQLException {
            Employee employee = new Employee();
            employee.setId(resultSet.getInt(1));
            employee.setName(resultSet.getString(2));
            employee.setOccupation(resultSet.getString(3));
            employee.setSalary(resultSet.getInt(4));
            employee.setAge(resultSet.getInt(5));
            employee.setJoinDate(resultSet.getDate(6));
            return employee;
        }
    }

}
