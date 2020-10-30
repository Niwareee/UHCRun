package fr.niware.uhcrun.game.event;

public abstract class Event {

    public void executeEvent() {
        activate();
    }

    public abstract void activate();
}
