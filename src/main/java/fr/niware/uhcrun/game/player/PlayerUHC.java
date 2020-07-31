package fr.niware.uhcrun.game.player;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.account.Rank;

import java.util.UUID;

public class UHCPlayer {

    private final UUID uuid;
    private final String name;
    private final Rank rank;
    private int killsGame;
    private final int killsAll;
    private final int wins;
    private boolean isWinner;

    public UHCPlayer(UUID uuid, String name, Rank rank, int killsGame, int killsAll, int wins){
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.killsGame = killsGame;
        this.killsAll = killsAll;
        this.wins = wins;
        this.isWinner = false;

        Main.getInstance().getPlayerManager().getPlayers().put(uuid, this);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
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

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }
}
