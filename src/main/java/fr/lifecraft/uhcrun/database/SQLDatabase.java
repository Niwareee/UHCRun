package fr.lifecraft.uhcrun.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.lifecraft.uhcrun.Main;

import java.sql.Connection;

public class SQLDatabase {

    private final String host, database, username, password;
    private final int port;
    private HikariDataSource hikariDataSource;

    private final Main main;

    public SQLDatabase(String host, String database, String username, String password, int port) {
        this.main = Main.getInstance();

        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public void connect() {
        try {
            if (!isConnected()) {
                final HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setJdbcUrl("jdbc:mysql://" + this.host + ":" + port + "/" + this.database + "?useUnicode=yes");
                hikariConfig.setUsername(username);
                hikariConfig.setPassword(password);
                hikariConfig.setMaxLifetime(600000L);
                hikariConfig.setIdleTimeout(300000L);
                hikariConfig.setLeakDetectionThreshold(300000L);
                hikariConfig.setMaximumPoolSize(8);
                hikariConfig.setConnectionTimeout(10000L);
                hikariConfig.addDataSourceProperty("useSSL", false);
                this.hikariDataSource = new HikariDataSource(hikariConfig);
                main.log("§aSuccessful connected to database !");
            }
        } catch (Exception e) {
            main.log("§cError to connect to MySQL:");
            main.getLogger().info(e.getMessage());
        }
    }

    public void updateTables() {
        try {
            if (isConnected()) {
                main.getSQLManager().update("TEST");
                main.log("§aSuccessful update table !");
            }
        } catch (Exception e) {
            main.log("§cUnable to update table to MySQL:");
            main.log(e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (isConnected()) {
                this.hikariDataSource.close();
                main.log("§aSuccessful disconnected from MySQL !");
            }
        } catch (Exception e) {
            main.log("§cUnable to disconnect from MySQL:");
            main.log(e.getMessage());
        }
    }

    public boolean isConnected() {
        return hikariDataSource != null && !hikariDataSource.isClosed();
    }

    public Connection getRessource() {
        try {
            return hikariDataSource.getConnection();
        } catch (Exception e) {
            main.log("§cUnable to get connection from MySQL:");
            main.log(e.getMessage());
            return null;
        }
    }

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }
}
