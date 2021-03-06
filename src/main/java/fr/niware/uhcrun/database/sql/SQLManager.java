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
        this.main = main;
        main.saveDefaultConfig();

        FileConfiguration config = main.getConfig();
        this.host = config.getString("sql.host");
        this.database = config.getString("sql.database");
        this.username = config.getString("sql.username");
        this.password = config.getString("sql.password");
        this.port = config.getInt("sql.port");

        this.connect();
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
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage("§cUpdate Failed:");
            Bukkit.getConsoleSender().sendMessage(exception.getMessage());
        }
    }

    public Object query(String qry, Function<ResultSet, Object> consumer) {
        try (Connection c = getResource();
             PreparedStatement state = c.prepareStatement(qry);
             ResultSet resultSet = state.executeQuery()) {
            return consumer.apply(resultSet);
        } catch (SQLException exception) {
            throw new IllegalStateException("Some things went wrong..." + exception.getMessage());
        }
    }

    public void query(String qry, Consumer<ResultSet> consumer) {
        try (Connection c = getResource();
             PreparedStatement state = c.prepareStatement(qry);
             ResultSet resultSet = state.executeQuery()) {
            consumer.accept(resultSet);
        } catch (SQLException exception) {
            throw new IllegalStateException("Some things went wrong..." + exception.getMessage());
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
