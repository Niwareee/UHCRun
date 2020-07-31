package fr.lifecraft.uhcrun.utils;

public enum State {

    PREGEN(false, false),
    WAITING(true, false),
    STARTING(true, false),
    TELEPORT(false, true),
    MINING(false, true),
    PVP(false, true),
    FINISH(false, false);

    private final boolean isInWait;
    private final boolean isInGame;
    private static State current = WAITING;

    State(boolean isInWait, boolean isInGame) {
        this.isInWait = isInWait;
        this.isInGame = isInGame;
    }

    public static boolean isState(State state) {
        return current == state;
    }

    public static void setState(State state) {
        current = state;
    }

    public static boolean isInWait() {
        return current.isInWait;
    }

    public static boolean isInGame() {
        return current.isInGame;
    }
}

