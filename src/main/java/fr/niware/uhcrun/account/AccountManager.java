package fr.niware.uhcrun.account;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.database.SQLManager;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.player.PlayerUHC;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountManager {

    private final Main main;
    private final SQLManager sqlManager;

    public List<Rank> ranks;

    public AccountManager(Main main) {
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
                main.getLogger().info("Load successfully rank_enum (" + ranks.size() + " ranks)");

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

    public void createAccount(UUID id) {
        sqlManager.update("INSERT INTO account_uhcrun (player_uuid, rankid, kills, wins, first_connection) VALUES " +
                "('" + id.toString() + "', '" + 0 + "', '" + 0 + "', '" + 0 + "', '" + new Timestamp(System.currentTimeMillis()) + "')");
        System.out.print("Successfully created " + id + "'s account");
    }

    public Rank getFromPower(int power) {
        return ranks.stream().filter(rank -> rank.getPower() == power).findAny().orElse(ranks.get(0));
    }

    public void sendFinishSQL() {
        long start = System.currentTimeMillis();
        Game game = main.getGame();

        sqlManager.update("INSERT INTO games_uhcrun (start, size_players, winner, finish) VALUES " +
                "('" + new Timestamp(game.getStartMillis()) + "', '" + game.getSizePlayers() + "', '" + game.getWinner().getName() + "', '" + new Timestamp(System.currentTimeMillis()) + "')");

        System.out.print(main.getPlayerManager().getPlayers().values().size());
        for (PlayerUHC uhcPlayer : main.getPlayerManager().getPlayers().values()) {
            if (!game.isWinner(uhcPlayer.getUUID())) {
                sqlManager.update("UPDATE account_uhcrun SET kills='" + uhcPlayer.getKillsAll() + uhcPlayer.getKillsGame() + "' WHERE player_uuid='" + uhcPlayer.getUUID() + "'");
            } else {
                System.out.print("dd");
                sqlManager.update("UPDATE account_uhcrun SET kills='" + uhcPlayer.getKillsAll() + uhcPlayer.getKillsGame() + "' SET wins='" + uhcPlayer.getWins() + 1 + "' WHERE player_uuid='" + uhcPlayer.getUUID() + "'");
            }
        }

        main.log("§aGame data successfully send to database in " + (System.currentTimeMillis() - start) + " ms !");
    }
}
