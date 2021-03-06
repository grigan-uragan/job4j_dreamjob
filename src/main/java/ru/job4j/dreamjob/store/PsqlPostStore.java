package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dreamjob.model.Post;

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

public class PsqlPostStore implements Store<Post> {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlPostStore.class);
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlPostStore() {
        Properties config = new Properties();
        try (BufferedReader reader = new BufferedReader(
                new FileReader("db.properties"))) {
            config.load(reader);
            Class.forName(config.getProperty("jdbc.driver"));
        } catch (IOException | ClassNotFoundException e) {
            LOG.error("Some trouble with download properties file", e);
        }
        pool.setDriverClassName(config.getProperty("jdbc.driver"));
        pool.setUrl(config.getProperty("jdbc.url"));
        pool.setUsername(config.getProperty("jdbc.username"));
        pool.setPassword(config.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    @Override
    public Collection<Post> findAll() {
        List<Post> result = new ArrayList<>();
        try (Connection connection = pool.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM post")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(new Post(resultSet.getInt("post_id"),
                        resultSet.getString("post_name")));
            }
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
        }
        return result;
    }

    private Post create(Post post) {
        try (Connection connection = pool.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO post (post_name) VALUES(?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getName());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                post.setId(resultSet.getInt("post_id"));
            }
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
        }
        return post;
    }

    private void update(Post element) {
        try (Connection connection = pool.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE post SET post_name = (?) WHERE post_id = (?)")) {
            statement.setString(1, element.getName());
            statement.setInt(2, element.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
        }
    }

    @Override
    public void save(Post element) {
        if (element.getId() == 0) {
            create(element);
        } else {
            update(element);
        }
    }

    @Override
    public Post findById(int id) {
        Post result = null;
        try (Connection connection = pool.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM post WHERE post_id = (?)")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = new Post(
                        resultSet.getInt("post_id"),
                        resultSet.getString("post_name"));
            }
        } catch (SQLException e) {
            LOG.error("some trouble with database", e);
        }
        return result;
    }

    private static final class Lazy {
        private static final Store<Post> INST = new PsqlPostStore();
    }

    public static Store<Post> instOf() {
        return Lazy.INST;
    }
}
