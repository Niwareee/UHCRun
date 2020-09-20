package fr.niware.uhcrun.database;

public class Rank {

    private final int power;
    private final String name;
    private final String prefix;
    private final String tab;
    private final String color;
    private final int order;

    public Rank(int power, String name, String prefix, String tab, String color, int order) {
        this.power = power;
        this.name = name;
        this.prefix = prefix;
        this.tab = tab;
        this.color = color;
        this.order = order;
    }

    public int getPower() {
        return power;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTab() {
        return tab;
    }

    public String getColor() {
        return color;
    }

    public int getOrder() {
        return order;
    }
}
