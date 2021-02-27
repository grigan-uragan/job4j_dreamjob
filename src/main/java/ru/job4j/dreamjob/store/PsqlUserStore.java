package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dreamjob.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlUserStore implements Store<User> {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlUserStore.class);
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlUserStore() {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("db.properties"))) {
            properties.load(reader);
            Class.forName(properties.getProperty("jdbc.driver"));
            pool.setUrl(properties.getProperty("jdbc.url"));
            pool.setUsername(properties.getProperty("jdbc.username"));
            pool.setPassword(properties.getProperty("jdbc.password"));
            pool.setMaxIdle(10);
            pool.setMinIdle(5);
            pool.setMaxOpenPreparedStatements(100);
        } catch (IOException | ClassNotFoundException e) {
            LOG.error("some problem with properties path or properties values", e);
        }
    }

    @Override
    public Collection<User> findAll() {
        List<User> result = new ArrayList<>();
        try (Connection connection = pool.getConnection();
              PreparedStatement statement = connection.prepareStatement(
                      "SELECT * FROM users"
              )) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("user_name"),
                        resultSet.getString("user_email"),
                        resultSet.getString("user_password")
                ));
            }
        } catch (SQLException e) {
            LOG.error("Problem with database connection or invalid sql query", e);
        }
        return result;
    }

    @Override
    public void save(User element) {
        try (Connection connection = pool.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO users (user_name, user_email, user_password)"
                             + "VALUES ((?), (?), (?))")) {
            statement.setString(1, element.getName());
            statement.setString(2, element.getEmail());
            statement.setString(3, element.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Trouble with sql query or database connection", e);
        }
    }

    @Override
    public User findById(int id) {
        try (Connection connection = pool.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM users WHERE user_id = (?)"
             )) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("user_name"),
                        resultSet.getString("user_email"),
                        resultSet.getString("user_password")
                );
            }
        } catch (SQLException e) {
            LOG.error("Trouble with sql query or database connection", e);
        }
        return null;
    }

    public User findByEmail(String email) {
        try (Connection connection = pool.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM users WHERE user_email = (?)"
        )) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("user_id"),
                        resultSet.getString("user_name"),
                        resultSet.getString("user_email"),
                        resultSet.getString("user_password"));
            }
        } catch (SQLException e) {
            LOG.error("Trouble with sql query or database connection", e);
        }
        return null;
    }

    public static PsqlUserStore instOf() {
        return Lazy.INST;
    }

    public static final class Lazy {
        public static final PsqlUserStore INST = new PsqlUserStore();
    }
}
