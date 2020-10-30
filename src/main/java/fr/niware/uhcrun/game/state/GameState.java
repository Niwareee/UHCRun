package fr.niware.uhcrun.game.state;

import net.minecraft.server.v1_8_R3.MinecraftServer;

public enum GameState {

    PRELOAD(0, "§eChargement..."),
    WAITING(1, "§bEn attente..."),
    STARTING(1, "§cDémarrage..."),
    TELEPORT(2, "§cTéléportation..."),
    MINING(2, "§cEn cours"),
    PVP(2, "§cEn cours"),
    FINISH(3, "§6Fin de la partie");

    private static GameState currentState;
    private final int value;
    private final String motd;

    GameState(int value, String motd) {
        this.value = value;
        this.motd = motd;
    }

    public static boolean isState(GameState state) {
        return currentState == state;
    }

    public static GameState getState() {
        return currentState;
    }

    public static void setState(GameState state) {
        currentState = state;
        MinecraftServer.getServer().setMotd(currentState.motd);
    }

    public static boolean isInWait() {
        return currentState.value == 1;
    }

    public static boolean isInGame() {
        return currentState.value == 2;
    }
}

