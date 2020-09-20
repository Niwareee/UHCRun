package fr.niware.uhcrun.game.state;

public enum GameState {

    PRELOAD(0),
    WAITING(1),
    STARTING(1),
    TELEPORT(2),
    MINING(2),
    PVP(2),
    FINISH(3);

    private static GameState current = WAITING;
    private final int value;

    GameState(int value) {
        this.value = value;
    }

    public static boolean isState(GameState state) {
        return current == state;
    }

    public static void setState(GameState state) {
        current = state;
    }

    public static boolean isInWait() {
        return current.value == 1;
    }

    public static boolean isInGame() {
        return current.value == 2;
    }
}

