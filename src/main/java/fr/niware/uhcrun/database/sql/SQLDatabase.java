package fr.niware.uhcrun.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.niware.uhcrun.UHCRun;
import org.bukkit.Bukkit;

import java.sql.Connection;

public class SQLDatabase {

    private HikariDataSource hikariDataSource;
    private final int port;
    private final String host, database, username, password;

    private final UHCRun main;

    public SQLDatabase(UHCRun main, String host, String database, String username, String password, int port) {
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
                long start = System.currentTimeMillis();
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
                main.log("§aSuccessful connected to database in " + (System.currentTimeMillis() - start) + " ms");

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
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                    main.getSQLManager().update("CREATE TABLE IF NOT EXISTS account_uhcrun (id int(11) AUTO_INCREMENT NOT NULL UNIQUE, player_uuid VARCHAR(255) NOT NULL UNIQUE, rankid INT NOT NULL, kills INT NOT NULL, wins INT NOT NULL, first_connection text NOT NULL)");
                    main.getSQLManager().update("CREATE TABLE IF NOT EXISTS games_uhcrun (id int(11) AUTO_INCREMENT NOT NULL UNIQUE, start text NOT NULL, size_players INT NOT NULL, winner TEXT NOT NULL, finish text NOT NULL)");
                    main.log("§aSuccessful update table !");
                });
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
        } catch (Exception exception) {
            main.log("§cUnable to disconnect from MySQL:");
            main.log(exception.getMessage());
        }
    }

    public boolean isConnected() {
        return hikariDataSource != null && !hikariDataSource.isClosed();
    }

    public Connection getResource() {
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
