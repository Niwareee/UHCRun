package fr.niware.uhcrun.database;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.database.sql.SQLManager;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.player.UHCPlayer;
import org.apache.commons.lang.time.DateFormatUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameDatabase {

    private final Main main;
    private final SQLManager sqlManager;

    public List<Rank> ranks;

    public GameDatabase(Main main) {
        this.main = main;
        this.sqlManager = main.getSQLManager();
        this.ranks = new ArrayList<>();

        initRanks();
    }

    public void initRanks() {
        sqlManager.query("SELECT * FROM rank_enum", rs -> {
            try {
                while (rs.next()) {
                    ranks.add(new Rank(rs.getInt("power"), rs.getString("name"), rs.getString("prefix"), rs.getString("tab"), rs.getString("chat_color"), rs.getInt("ordre")));
                }
                main.getLogger().info("Load successfully " + ranks.size() + " ranks");
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public int[] getDatabaseAccount(UUID uuid) {
        long start = System.currentTimeMillis();
        int[] data = new int[3];
        sqlManager.query("SELECT * FROM account_uhcrun WHERE player_uuid='" + uuid + "'", rs -> {
            try {
                if (!rs.next()) {
                    createAccount(uuid);
                    return;
                }

                data[0] = rs.getInt("rankid");
                data[1] = rs.getInt("kills");
                data[2] = rs.getInt("wins");
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
        System.out.print("Load connection SQL in " + (System.currentTimeMillis() - start) + " MS");
        return data;
    }

    public void createAccount(UUID uuid){
        try {
            PreparedStatement q = sqlManager.getResource().prepareStatement("INSERT INTO account_uhcrun (player_uuid, rankid, kills, wins, first_connection) VALUES (?,'0','0','0',?)");
            q.setString(1, uuid.toString());
            q.setString(2, DateFormatUtils.format(System.currentTimeMillis(), "dd/MM/yyyy 'à' hh:mm:ss"));
            q.execute();
            q.close();
            System.out.print("Successfully created " + uuid + "'s account");
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public Rank getFromPower(int power) {
        return ranks.stream().filter(rank -> rank.getPower() == power).findAny().orElse(ranks.get(0));
    }

    public void sendFinishSQL() {
        long start = System.currentTimeMillis();
        Game game = main.getGame();

        insertNewGame(game.getStartMillis(), game.getSizePlayers(), game.getWinner().getName(), System.currentTimeMillis());

        for (UHCPlayer uhcPlayer : main.getPlayerManager().getPlayers()) {
            if (game.isWinner(uhcPlayer.getUUID())) {
                try {
                    PreparedStatement q = sqlManager.getResource().prepareStatement("UPDATE account_uhcrun SET kills = ?, wins = ? WHERE player_uuid = ?");
                    q.setInt(1, uhcPlayer.getKillsAll() + uhcPlayer.getKillsGame());
                    q.setInt(2, uhcPlayer.getWins() + 1);
                    q.setString(3, uhcPlayer.getUUID().toString());
                    q.execute();
                    q.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                continue;
            }

            try {
                PreparedStatement q = sqlManager.getResource().prepareStatement("UPDATE account_uhcrun SET kills = ? WHERE player_uuid = ?");
                q.setInt(1, uhcPlayer.getKillsAll() + uhcPlayer.getKillsGame());
                q.setString(2, uhcPlayer.getUUID().toString());
                q.execute();
                q.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        main.log("§aGame data successfully send to database in " + (System.currentTimeMillis() - start) + " ms !");
    }

    public void insertNewGame(long start, int size_players, String winner, long finish){
        try {
            PreparedStatement q = sqlManager.getResource().prepareStatement("INSERT INTO games_uhcrun (start, size_players, winner, finish) VALUES (?,?,?,?)");
            q.setString(1, DateFormatUtils.format(start, "dd/MM/yyyy 'à' hh:mm:ss"));
            q.setInt(2, size_players);
            q.setString(3, winner);
            q.setString(4, DateFormatUtils.format(finish, "dd/MM/yyyy 'à' hh:mm:ss"));
            q.execute();
            q.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
