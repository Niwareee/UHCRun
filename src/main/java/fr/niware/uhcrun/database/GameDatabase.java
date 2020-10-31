package fr.niware.uhcrun.database;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.database.sql.SQLManager;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.player.UHCPlayer;
import org.apache.commons.lang.time.DateFormatUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameDatabase {

    private final UHCRun main;
    private final Game game;
    private final SQLManager sqlManager;

    public List<Rank> ranks = new ArrayList<>();

    public GameDatabase(UHCRun main) {
        this.main = main;
        this.game = main.getGame();
        this.sqlManager = main.getSQLManager();

        this.initRanks();
    }

    public void initRanks() {
        try {
            PreparedStatement statement = sqlManager.getResource().prepareStatement("SELECT * FROM rank_enum");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ranks.add(new Rank(resultSet.getInt("power"), resultSet.getString("name"), resultSet.getString("prefix"), resultSet.getString("tab"), resultSet.getString("chat_color"), resultSet.getInt("ordre")));
            }
            main.log("§aLoad successfully " + ranks.size() + " ranks.");
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public int[] getDatabaseAccount(UUID uuid) {
        long start = System.currentTimeMillis();
        int[] data = new int[3];
        try {
            PreparedStatement statement = sqlManager.getResource().prepareStatement("SELECT rankid,kills,wins FROM account_uhcrun WHERE player_uuid=?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                data[0] = resultSet.getInt("rankid");
                data[1] = resultSet.getInt("kills");
                data[2] = resultSet.getInt("wins");
                statement.close();
                resultSet.close();
                System.out.print("Load connection SQL in " + (System.currentTimeMillis() - start) + " MS");
                return data;
            }

            statement.close();
            this.createAccount(uuid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return data;
    }

    public void createAccount(UUID uuid){
        try {
            PreparedStatement statement = sqlManager.getResource().prepareStatement("INSERT INTO account_uhcrun (player_uuid, rankid, kills, wins, first_connection) VALUES (?,'0','0','0',?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, DateFormatUtils.format(System.currentTimeMillis(), "dd/MM/yyyy 'à' hh:mm:ss"));
            statement.execute();
            statement.close();
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
        this.insertNewGame(game.getStartMillis(), game.getSizePlayers(), game.getWinner().getName(), System.currentTimeMillis());

        for (UHCPlayer uhcPlayer : main.getPlayerManager().getPlayers()) {
            if (game.isWinner(uhcPlayer.getUUID())) {
                try {
                    PreparedStatement statement = sqlManager.getResource().prepareStatement("UPDATE account_uhcrun SET kills = ?, wins = ? WHERE player_uuid = ?");
                    statement.setInt(1, uhcPlayer.getKillsAll() + uhcPlayer.getKillsGame());
                    statement.setInt(2, uhcPlayer.getWins() + 1);
                    statement.setString(3, uhcPlayer.getUUID().toString());
                    statement.execute();
                    statement.close();
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
                continue;
            }

            try {
                PreparedStatement statement = sqlManager.getResource().prepareStatement("UPDATE account_uhcrun SET kills = ? WHERE player_uuid = ?");
                statement.setInt(1, uhcPlayer.getKillsAll() + uhcPlayer.getKillsGame());
                statement.setString(2, uhcPlayer.getUUID().toString());
                statement.execute();
                statement.close();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }

        main.log("§aGame data successfully send to database in " + (System.currentTimeMillis() - start) + " ms !");
    }

    public void insertNewGame(long start, int size_players, String winner, long finish){
        try {
            PreparedStatement statement = sqlManager.getResource().prepareStatement("INSERT INTO games_uhcrun (start, size_players, winner, finish) VALUES (?,?,?,?)");
            statement.setString(1, DateFormatUtils.format(start, "dd/MM/yyyy 'à' hh:mm:ss"));
            statement.setInt(2, size_players);
            statement.setString(3, winner);
            statement.setString(4, DateFormatUtils.format(finish, "dd/MM/yyyy 'à' hh:mm:ss"));
            statement.execute();
            statement.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
