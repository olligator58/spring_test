package dao;

import domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmployeeDao {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final EmployeeMapper employeeMapper = new EmployeeMapper();

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeById(int id) {
        String sql = "select * from employee where id = ?";
        return jdbcTemplate.queryForObject(sql, employeeMapper, id);
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByMinSalary(int salary) {
        String sql = "select * from employee where salary >= :salary";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("salary", salary);
        return namedParameterJdbcTemplate.query(sql, params, employeeMapper);
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        String sql = "select * from employee";
        return jdbcTemplate.query(sql, employeeMapper);
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
