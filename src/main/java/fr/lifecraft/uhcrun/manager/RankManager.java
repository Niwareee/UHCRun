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

    private Main main;
    private SQLManager sqlManager;

    public List<Rank> ranks = new ArrayList<>();

    public RankManager(){
        this.main = Main.getInstance();
        this.sqlManager = main.getSQLManager();

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

    public void addKills(UUID uuid, Rank rank) {
        sqlManager.update("UPDATE lifeplayers SET kills='" + rank.getPower() + "' WHERE player_uuid='" + uuid + "'");
    }

    public void addDeath(UUID uuid, Rank rank) {
        sqlManager.update("UPDATE lifeplayers SET rankid='" + rank.getPower() + "' WHERE player_uuid='" + uuid + "'");
    }

    public Rank getFromPower(int power) {
        return ranks.stream().filter(rank -> rank.getPower() == power).findAny().orElse(ranks.get(0));
    }

    /*public static RankManager getDatabaseRank(UUID uuid) {
        RankManager rank = null;
        try {
            PreparedStatement sts = main.getConnection().prepareStatement("SELECT rankid FROM lifeplayers WHERE player_uuid=?");
            sts.setString(1, uuid.toString());
            ResultSet rs = sts.executeQuery();

            if (rs.next()) {
                rank = getFromPower(rs.getInt("rankid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rank;
    }

    public static Integer getDatabaseCoins(UUID uuid) {
        int coins = 0;
        try {
            PreparedStatement sts = main.getConnection().prepareStatement("SELECT coins FROM lifeplayers WHERE player_uuid=?");
            sts.setString(1, uuid.toString());
            ResultSet rs = sts.executeQuery();

            if (rs.next()) {
                coins = rs.getInt("coins");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coins;
    }

    public static void addCouronne(Player player, long coins) {
        try {
            PreparedStatement sts = Main.getInstance().getConnection().prepareStatement("SELECT coins FROM lifeplayers WHERE player_uuid=?");
            sts.setString(1, player.getUniqueId().toString());
            ResultSet rs = sts.executeQuery();

            if (rs.next()) {
                long money = rs.getLong("coins");
                sts.close();
                sts = Main.getInstance().getConnection().prepareStatement("UPDATE lifeplayers SET coins=? WHERE player_uuid=?");
                sts.setLong(1, (coins + money));
                sts.setString(2, player.getUniqueId().toString());
                sts.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}
