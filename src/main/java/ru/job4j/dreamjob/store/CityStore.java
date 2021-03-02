package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class CityStore {
    private static final Logger LOG = LoggerFactory.getLogger(CityStore.class);
    private final BasicDataSource pool = new BasicDataSource();

    private CityStore() {
        Properties config = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("db.properties"))) {
            config.load(reader);
            Class.forName(config.getProperty("jdbc.driver"));
            pool.setUrl(config.getProperty("jdbc.url"));
            pool.setUsername(config.getProperty("jdbc.username"));
            pool.setPassword(config.getProperty("jdbc.password"));
            pool.setDriverClassName(config.getProperty("jdbc.driver"));
        } catch (IOException | ClassNotFoundException e) {
            LOG.error("Some trouble with properties path or property", e);
        }
        pool.setUrl(config.getProperty("jdbc.url"));
        pool.setUsername(config.getProperty("jdbc.username"));
        pool.setPassword(config.getProperty("jdbc.password"));
        pool.setDriverClassName(config.getProperty("jdbc.driver"));
        pool.setMinIdle(5);
        pool.setMaxIdle(100);
        pool.setMaxOpenPreparedStatements(100);
    }

    /**
     *
     * @param city
     * @return city_id which associated with city_name on the city table
     * or -1 if fail
     */
    public int saveCity(String city) {
        int id = -1;
        try (Connection connection = pool.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "insert into city (city_name) values (?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, city);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt("city_id");
            }
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
        }
        return id;
    }

    public Map<Integer, String> allCities() {
        Map<Integer, String> result = new HashMap<>();
        try (Connection connection = pool.getConnection();
             PreparedStatement statement = connection.prepareStatement(
             "select * from city")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.put(resultSet.getInt("city_id"),
                        resultSet.getString("city_name"));
            }
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
        }
        return result;
    }

    public String findCityById(int id) {
        try (Connection connection = pool.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "select city_name from city where city_id = (?)")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("city_name");
            }
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
        }
        return "city not found";
    }

    public static CityStore instOf() {
        return Lazy.INST;
    }

    private static final class Lazy {
        private static final CityStore INST = new CityStore();
    }
}
