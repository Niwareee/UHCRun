package fr.lifecraft.uhcrun.utils;

public enum State {

    PREGEN(false),
    WAITING(false),
    TELEPORT(true),
    MINING(true),
    PVP(true),
    FINISH(false);

    private static boolean isInGame;
    private static State current = WAITING;

    State(boolean isInGame) {
    }

    public static boolean isState(State state) {
        return current == state;
    }

    public static void setState(State state) {
        current = state;
    }

    public static boolean isInGame() {
        return isInGame;
    }
}

