package fr.lifecraft.uhcrun.database;

import fr.lifecraft.uhcrun.Main;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

public class SQLManager {

    private static SQLDatabase sqlDatabase;
    private final String host, database, username, password;
    private final int port;

    public SQLManager(Main main) {

        main.saveDefaultConfig();
        this.host = main.getConfig().getString("sql.host");
        this.database = main.getConfig().getString("sql.database");
        this.username = main.getConfig().getString("sql.username");
        this.password = main.getConfig().getString("sql.password");
        this.port = main.getConfig().getInt("sql.port");

        connect();
    }

    public Connection getRessource() {
        return sqlDatabase.getRessource();
    }

    public SQLDatabase getSQLDatabase() {
        return sqlDatabase;
    }

    public void update(String qry) {
        try (Connection c = getRessource();
             PreparedStatement state = c.prepareStatement(qry)) {
            state.executeUpdate();
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage("§cUpdate Failed:");
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
        }
    }

    public Object query(String qry, Function<ResultSet, Object> consumer) {
        try (Connection c = getRessource();
             PreparedStatement state = c.prepareStatement(qry);
             ResultSet resultSet = state.executeQuery()) {
            return consumer.apply(resultSet);
        } catch (SQLException e) {
            throw new IllegalStateException("Some things went wrong..." + e.getMessage());
        }
    }

    public void query(String qry, Consumer<ResultSet> consumer) {
        try (Connection c = getRessource();
             PreparedStatement state = c.prepareStatement(qry);
             ResultSet resultSet = state.executeQuery()) {
            consumer.accept(resultSet);
        } catch (SQLException e) {
            throw new IllegalStateException("Some things went wrong..." + e.getMessage());
        }
    }

    public void connect() {
        sqlDatabase = new SQLDatabase(host, database, username, password, port);
        sqlDatabase.connect();
    }

    public void disconnect() {
        sqlDatabase.disconnect();
    }
}
