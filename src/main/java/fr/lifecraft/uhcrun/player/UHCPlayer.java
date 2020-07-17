package fr.lifecraft.uhcrun.player;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.rank.Rank;

import java.util.UUID;

public class UHCPlayer {

    private UUID id;
    private int kills;
    private Rank rank;

    public UHCPlayer(UUID id, int kills, Rank rank){
        this.id = id;
        this.kills = kills;
        this.rank = rank;

        Main.getInstance().getPlayerManager().getPlayers().put(id, this);
    }

    public UUID getId() {
        return id;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public Rank getRank() {
        return rank;
    }
}
