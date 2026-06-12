package com.example.contactmanager.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.contactmanager.domain.Person;

/**
 * Spring JDBC implementation of {@link PersonDao} backed by the
 * {@code person} table.
 */
@Repository
public class JdbcPersonDao implements PersonDao {

    private static final String SELECT_ALL = """
            SELECT person_id, first_name, last_name, email_address, street_address, city, state, zip_code
            FROM person
            ORDER BY last_name, first_name, person_id
            """;

    private static final String SELECT_PAGE = """
            SELECT person_id, first_name, last_name, email_address, street_address, city, state, zip_code
            FROM person
            ORDER BY last_name, first_name, person_id
            OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY
            """;

    private static final String COUNT_ALL = "SELECT COUNT(*) FROM person";

    private static final String SELECT_BY_ID = """
            SELECT person_id, first_name, last_name, email_address, street_address, city, state, zip_code
            FROM person
            WHERE person_id = :id
            """;

    private static final String INSERT = """
            INSERT INTO person (first_name, last_name, email_address, street_address, city, state, zip_code)
            VALUES (:firstName, :lastName, :emailAddress, :streetAddress, :city, :state, :zipCode)
            """;

    private static final String UPDATE = """
            UPDATE person
            SET first_name = :firstName,
                last_name = :lastName,
                email_address = :emailAddress,
                street_address = :streetAddress,
                city = :city,
                state = :state,
                zip_code = :zipCode
            WHERE person_id = :id
            """;

    private static final String DELETE = "DELETE FROM person WHERE person_id = :id";

    private static final RowMapper<Person> PERSON_ROW_MAPPER = new PersonRowMapper();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param jdbcTemplate the shared JDBC template
     */
    public JdbcPersonDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Person> findAll() {
        return jdbcTemplate.query(SELECT_ALL, PERSON_ROW_MAPPER);
    }

    @Override
    public List<Person> findPage(long offset, int limit) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("offset", offset)
                .addValue("limit", limit);
        return jdbcTemplate.query(SELECT_PAGE, params, PERSON_ROW_MAPPER);
    }

    @Override
    public long count() {
        Long count = jdbcTemplate.queryForObject(COUNT_ALL, new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public Optional<Person> findById(long id) {
        List<Person> results = jdbcTemplate.query(SELECT_BY_ID, new MapSqlParameterSource("id", id), PERSON_ROW_MAPPER);
        return results.stream().findFirst();
    }

    @Override
    public long insert(Person person) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT, parameterSource(person), keyHolder, new String[] { "person_id" });
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Insert did not return a generated key for person");
        }
        return key.longValue();
    }

    @Override
    public void update(Person person) {
        int rows = jdbcTemplate.update(UPDATE, parameterSource(person).addValue("id", person.getId()));
        if (rows == 0) {
            throw new PersonNotFoundException(person.getId());
        }
    }

    @Override
    public void delete(long id) {
        int rows = jdbcTemplate.update(DELETE, new MapSqlParameterSource("id", id));
        if (rows == 0) {
            throw new PersonNotFoundException(id);
        }
    }

    private static MapSqlParameterSource parameterSource(Person person) {
        return new MapSqlParameterSource()
                .addValue("firstName", person.getFirstName())
                .addValue("lastName", person.getLastName())
                .addValue("emailAddress", person.getEmailAddress())
                .addValue("streetAddress", person.getStreetAddress())
                .addValue("city", person.getCity())
                .addValue("state", person.getState())
                .addValue("zipCode", person.getZipCode());
    }

    /**
     * Maps rows of the {@code person} table to {@link Person} instances.
     */
    private static final class PersonRowMapper implements RowMapper<Person> {

        @Override
        public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
            Person person = new Person();
            person.setId(rs.getLong("person_id"));
            person.setFirstName(rs.getString("first_name"));
            person.setLastName(rs.getString("last_name"));
            person.setEmailAddress(rs.getString("email_address"));
            person.setStreetAddress(rs.getString("street_address"));
            person.setCity(rs.getString("city"));
            person.setState(rs.getString("state"));
            person.setZipCode(rs.getString("zip_code"));
            return person;
        }
    }
}
