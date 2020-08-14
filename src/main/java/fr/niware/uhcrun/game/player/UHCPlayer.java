package fr.niware.uhcrun.game.player;

import fr.niware.uhcrun.account.Rank;

import java.util.UUID;

public class UHCPlayer {

    private final UUID uuid;
    private final Rank rank;
    private int killsGame;
    private final int killsAll;
    private final int wins;

    public UHCPlayer(UUID uuid, Rank rank, int killsAll, int wins){
        this.uuid = uuid;
        this.rank = rank;
        this.killsGame = 0;
        this.killsAll = killsAll;
        this.wins = wins;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getKillsGame() {
        return killsGame;
    }

    public void setKillsGame(int killsGame) {
        this.killsGame = killsGame;
    }

    public int getKillsAll() {
        return killsAll;
    }

    public Rank getRank() {
        return rank;
    }

    public int getWins() {
        return wins;
    }
}
