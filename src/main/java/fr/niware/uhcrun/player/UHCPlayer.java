package fr.niware.uhcrun.player;

import fr.niware.uhcrun.database.Rank;
import fr.niware.uhcrun.player.state.PlayerState;

import java.util.UUID;

public class UHCPlayer {

    private final UUID uuid;
    private PlayerState playerState;
    private final Rank rank;
    private int killsGame;
    private final int killsAll;
    private final int wins;

    public UHCPlayer(UUID uuid, PlayerState playerState, Rank rank, int killsAll, int wins){
        this.uuid = uuid;
        this.playerState = playerState;
        this.rank = rank;
        this.killsGame = 0;
        this.killsAll = killsAll;
        this.wins = wins;
    }

    public UUID getUUID() {
        return uuid;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
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
