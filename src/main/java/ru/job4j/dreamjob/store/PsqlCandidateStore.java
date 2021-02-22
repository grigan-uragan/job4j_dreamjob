package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dreamjob.model.Candidate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlCandidateStore implements Store<Candidate> {
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlCandidateStore() {
        Properties config = new Properties();
        try (BufferedReader reader =
                     new BufferedReader(new FileReader("src/main/resources/db.properties"))) {
            config.load(reader);
            Class.forName(config.getProperty("jdbc.driver"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        pool.setDriverClassName(config.getProperty("jdbc.driver"));
        pool.setUrl(config.getProperty("jdbc.url"));
        pool.setUsername(config.getProperty("jdbc.username"));
        pool.setPassword(config.getProperty("jdbc.password"));
        pool.setMaxIdle(10);
        pool.setMinIdle(5);
        pool.setMaxOpenPreparedStatements(100);
    }

    @Override
    public Collection<Candidate> findAll() {
        List<Candidate> result = new ArrayList<>();
        try (Connection connection = pool.getConnection();
              PreparedStatement statement = connection.prepareStatement(
                      "SELECT * FROM candidates"
              )) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(new Candidate(resultSet.getInt("candidate_id"),
                        resultSet.getString("candidate_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(Candidate element) {
        if (element.getId() == 0) {
            create(element);
        } else {
            update(element);
        }
    }

    private void update(Candidate element) {
        try (Connection connection = pool.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE candidates SET candidate_name = (?) WHERE candidate_id = (?)"
        )) {
            statement.setString(1, element.getName());
            statement.setInt(2, element.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Candidate create(Candidate element) {
        try (Connection connection = pool.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO candidates (candidate_name) VALUES (?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, element.getName());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                element.setId(resultSet.getInt("candidate_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return element;
    }

    @Override
    public Candidate findById(int id) {
        Candidate result = null;
        try (Connection connection = pool.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM candidates WHERE candidate_id = (?)"
        )) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = new Candidate(resultSet.getInt("candidate_id"),
                        resultSet.getString("candidate_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Store<Candidate> instOf() {
        return Lazy.INST;
    }

    private static final class Lazy {
        private static final Store<Candidate> INST = new PsqlCandidateStore();
    }
}
