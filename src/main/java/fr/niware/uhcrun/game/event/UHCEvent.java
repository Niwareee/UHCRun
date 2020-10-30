package fr.niware.uhcrun.game.event;

public abstract class UHCEvent {

    public void executeEvent() {
        activate();
    }

    public abstract void activate();
}
