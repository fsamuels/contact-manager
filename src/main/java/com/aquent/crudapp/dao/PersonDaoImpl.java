package com.aquent.crudapp.dao;

import com.aquent.crudapp.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Spring JDBC implementation of {@link PersonDao}.
 */
@Repository
public class PersonDaoImpl implements PersonDao {

    private static final String SQL_LIST   = "SELECT * FROM person ORDER BY last_name, first_name";
    private static final String SQL_READ   = "SELECT * FROM person WHERE person_id = ?";
    private static final String SQL_UPDATE =
        "UPDATE person SET first_name=?, last_name=?, email=?, street=?, city=?, state=?, zip=? WHERE person_id=?";
    private static final String SQL_DELETE = "DELETE FROM person WHERE person_id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Person> listPeople() {
        return jdbcTemplate.query(SQL_LIST, new PersonRowMapper());
    }

    @Override
    public Person readPerson(Integer personId) {
        return jdbcTemplate.queryForObject(SQL_READ, new PersonRowMapper(), personId);
    }

    @Override
    public Integer createPerson(Person person) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("person")
                .usingGeneratedKeyColumns("person_id");
        return insert.executeAndReturnKey(new BeanPropertySqlParameterSource(person)).intValue();
    }

    @Override
    public void updatePerson(Person person) {
        jdbcTemplate.update(SQL_UPDATE,
                person.getFirstName(),
                person.getLastName(),
                person.getEmail(),
                person.getStreet(),
                person.getCity(),
                person.getState(),
                person.getZip(),
                person.getPersonId());
    }

    @Override
    public void deletePerson(Integer personId) {
        jdbcTemplate.update(SQL_DELETE, personId);
    }

    /**
     * Maps a result set row to a {@link Person}.
     */
    private static final class PersonRowMapper implements RowMapper<Person> {
        @Override
        public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
            Person p = new Person();
            p.setPersonId(rs.getInt("person_id"));
            p.setFirstName(rs.getString("first_name"));
            p.setLastName(rs.getString("last_name"));
            p.setEmail(rs.getString("email"));
            p.setStreet(rs.getString("street"));
            p.setCity(rs.getString("city"));
            p.setState(rs.getString("state"));
            p.setZip(rs.getString("zip"));
            return p;
        }
    }
}
