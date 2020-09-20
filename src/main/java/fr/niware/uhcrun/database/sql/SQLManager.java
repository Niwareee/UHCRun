package fr.niware.uhcrun.database.sql;

import fr.niware.uhcrun.UHCRun;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

public class SQLManager {

    private final UHCRun main;
    private static SQLDatabase sqlDatabase;
    private final String host, database, username, password;
    private final int port;

    public SQLManager(UHCRun main) {

        main.saveDefaultConfig();
        this.main = main;

        FileConfiguration config = main.getConfig();
        this.host = config.getString("sql.host");
        this.database = config.getString("sql.database");
        this.username = config.getString("sql.username");
        this.password = config.getString("sql.password");
        this.port = config.getInt("sql.port");

        connect();
    }

    public Connection getResource() {
        return sqlDatabase.getResource();
    }

    public SQLDatabase getSQLDatabase() {
        return sqlDatabase;
    }

    public void update(String qry) {
        try (Connection c = getResource();
             PreparedStatement state = c.prepareStatement(qry)) {
             state.executeUpdate();
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage("§cUpdate Failed:");
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
        }
    }

    public Object query(String qry, Function<ResultSet, Object> consumer) {
        try (Connection c = getResource();
             PreparedStatement state = c.prepareStatement(qry);
             ResultSet resultSet = state.executeQuery()) {
            return consumer.apply(resultSet);
        } catch (SQLException e) {
            throw new IllegalStateException("Some things went wrong..." + e.getMessage());
        }
    }

    public void query(String qry, Consumer<ResultSet> consumer) {
        try (Connection c = getResource();
             PreparedStatement state = c.prepareStatement(qry);
             ResultSet resultSet = state.executeQuery()) {
            consumer.accept(resultSet);
        } catch (SQLException e) {
            throw new IllegalStateException("Some things went wrong..." + e.getMessage());
        }
    }

    public void connect() {
        sqlDatabase = new SQLDatabase(main, host, database, username, password, port);
        sqlDatabase.connect();
    }

    public void disconnect() {
        sqlDatabase.disconnect();
    }
}
