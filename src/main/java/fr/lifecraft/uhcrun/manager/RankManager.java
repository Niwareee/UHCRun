package fr.lifecraft.uhcrun.manager;

import fr.lifecraft.uhcrun.database.SQLManager;
import fr.lifecraft.uhcrun.rank.Rank;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.lifecraft.uhcrun.Main;

import java.sql.*;
import java.util.*;

public class RankManager {

    private final Main main;
    private final SQLManager sqlManager;

    public List<Rank> ranks;

    public RankManager(){
        this.main = Main.getInstance();
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
                main.getLogger().info("Load " + ranks.size() + " ranks");

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public Rank getDatabaseRank(UUID uuid) {
        return (Rank) sqlManager.query("SELECT rankid FROM lifeplayers WHERE player_uuid='" + uuid.toString() + "'", rs -> {
            try {
                if (rs.next()) {
                    return getFromPower(rs.getInt("rankid"));
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return getFromPower(0);
        });
    }

    public void createAccount(Player player) {
        sqlManager.update("INSERT INTO lifeplayers (player_uuid, rankid, ip, firstjoin, coins) VALUES " +
                "('" + player.getUniqueId().toString() + "', '" + 0 + "', '" + player.getAddress().getAddress().getHostAddress() + "', '" + DateFormatUtils.format(System.currentTimeMillis(), "dd/MM/yyyy") + "', '" + 0 + "')");
        System.out.print("Successfully created " + player.getName() + "'s account");
    }

    public void setRank(UUID uuid, Rank rank) {
        sqlManager.update("UPDATE lifeplayers SET rankid='" + rank.getPower() + "' WHERE player_uuid='" + uuid + "'");
    }

    public Rank getFromPower(int power) {
        return ranks.stream().filter(rank -> rank.getPower() == power).findAny().orElse(ranks.get(0));
    }
}
