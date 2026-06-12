package com.aquent.contactmanager.dao;

import com.aquent.contactmanager.model.Person;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * Spring JDBC implementation of {@link PersonDao} backed by a relational database.
 *
 * <p>Uses {@link NamedParameterJdbcTemplate} for readable, injection-safe parameter binding.
 * All SQL is parameterized; no user-supplied value is ever concatenated into a statement.</p>
 */
@Repository
public class JdbcPersonDao implements PersonDao {

    /** Maps a result-set row onto a {@link Person} instance. */
    private static final RowMapper<Person> PERSON_ROW_MAPPER = (rs, rowNum) -> {
        Person person = new Person();
        person.setId(rs.getInt("id"));
        person.setFirstName(rs.getString("first_name"));
        person.setLastName(rs.getString("last_name"));
        person.setEmailAddress(rs.getString("email_address"));
        person.setStreetAddress(rs.getString("street_address"));
        person.setCity(rs.getString("city"));
        person.setState(rs.getString("state"));
        person.setZipCode(rs.getString("zip_code"));
        return person;
    };

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Creates the DAO.
     *
     * @param dataSource the configured {@link DataSource} (constructor injection)
     */
    public JdbcPersonDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Person> findAll() {
        String sql = "SELECT id, first_name, last_name, email_address, street_address, city, state, zip_code "
                + "FROM person ORDER BY last_name, first_name";
        return jdbcTemplate.query(sql, PERSON_ROW_MAPPER);
    }

    @Override
    public Person findById(int id) {
        String sql = "SELECT id, first_name, last_name, email_address, street_address, city, state, zip_code "
                + "FROM person WHERE id = :id";
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        List<Person> results = jdbcTemplate.query(sql, params, PERSON_ROW_MAPPER);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public int create(Person person) {
        String sql = "INSERT INTO person "
                + "(first_name, last_name, email_address, street_address, city, state, zip_code) "
                + "VALUES (:firstName, :lastName, :emailAddress, :streetAddress, :city, :state, :zipCode)";
        SqlParameterSource params = new BeanPropertySqlParameterSource(person);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("No generated key returned when creating person.");
        }
        return key.intValue();
    }

    @Override
    public void update(Person person) {
        String sql = "UPDATE person SET "
                + "first_name = :firstName, last_name = :lastName, email_address = :emailAddress, "
                + "street_address = :streetAddress, city = :city, state = :state, zip_code = :zipCode "
                + "WHERE id = :id";
        SqlParameterSource params = new BeanPropertySqlParameterSource(person);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM person WHERE id = :id";
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }
}
