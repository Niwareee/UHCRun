package fr.niware.uhcrun.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.niware.uhcrun.Main;
import org.bukkit.Bukkit;

import java.sql.Connection;

public class SQLDatabase {

    private final String host, database, username, password;
    private final int port;
    private HikariDataSource hikariDataSource;

    private final Main main;

    public SQLDatabase(Main main, String host, String database, String username, String password, int port) {
        this.main = main;

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

                updateTables();
            }
        } catch (Exception e) {
            main.log("§cError to connect to MySQL:");
            main.log(e.getMessage());
        }
    }

    public void updateTables() {
        try {
            if (isConnected()) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
                    main.getSQLManager().update("CREATE TABLE IF NOT EXISTS account_uhcrun (id int(11) AUTO_INCREMENT NOT NULL UNIQUE, player_uuid VARCHAR(255) NOT NULL UNIQUE, rankid INT NOT NULL, kills INT NOT NULL, wins INT NOT NULL, first_connection TIMESTAMP NOT NULL)");
                    main.getSQLManager().update("CREATE TABLE IF NOT EXISTS games_uhcrun (id int(11) AUTO_INCREMENT NOT NULL UNIQUE, start TIMESTAMP NOT NULL, size_players INT NOT NULL, winner TEXT NOT NULL, finish TIMESTAMP NOT NULL)");
                    main.log("§aSuccessful update table !");
                }, 1L);
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
