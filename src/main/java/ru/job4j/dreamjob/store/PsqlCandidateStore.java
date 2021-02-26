package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dreamjob.model.Candidate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class PsqlCandidateStore implements Store<Candidate> {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlCandidateStore.class);
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlCandidateStore() {
        Properties config = new Properties();
        try (BufferedReader reader =
                     new BufferedReader(new FileReader("db.properties"))) {
            config.load(reader);
            Class.forName(config.getProperty("jdbc.driver"));
        } catch (IOException | ClassNotFoundException e) {
            LOG.error("Some trouble with download properties file", e);
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
                      "SELECT * FROM candidates AS c LEFT JOIN"
                              + " photo AS p ON c.photo_id = p.photo_id"
              )) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(new Candidate(resultSet.getInt("candidate_id"),
                        resultSet.getString("candidate_name"),
                        resultSet.getInt("photo_id")));
            }
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
        }
        return result;
    }

    public Map<Integer, String> allImages() {
        Map<Integer, String> result = new HashMap<>();
        try (Connection connection = pool.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM photo"
        )) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.put(resultSet.getInt("photo_id"),
                        resultSet.getString("photo_path"));
            }
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
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
                "UPDATE candidates SET candidate_name = (?), photo_id = (?)"
                        + " WHERE candidate_id = (?)"
        )) {
            statement.setString(1, element.getName());
            statement.setInt(2, element.getPhotoId());
            statement.setInt(3, element.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
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
            LOG.error("some trouble with database", e);
        }
        return element;
    }

    @Override
    public Candidate findById(int id) {
        Candidate result = null;
        try (Connection connection = pool.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM candidates AS c LEFT JOIN photo AS p ON c.photo_id = p.photo_id"
                        + " WHERE candidate_id = (?)"
        )) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = new Candidate(resultSet.getInt("candidate_id"),
                        resultSet.getString("candidate_name"));
            }
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
        }
        return result;
    }

    public int savePhoto(String path) {
        int result = -1;
        try (Connection connection = pool.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "insert into photo (photo_path) values (?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {
            statement.setString(1, path);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                result = resultSet.getInt("photo_id");
            }
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
        }
        return result;
    }

    public static PsqlCandidateStore instOf() {
        return Lazy.INST;
    }

    private static final class Lazy {
        private static final PsqlCandidateStore INST = new PsqlCandidateStore();
    }
}
