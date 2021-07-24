package edu.buet.data;

public class Player {
//id;name;age;position;preferred-foot;club-id;country-id;height;weight;number;salary;value;image-link;
    public enum Foot {LEFT, RIGHT}
    private final int id;
    private final String name;
    private final int age;
    private final Position position;
    private final Country country;
    private final Foot preferredFoot;
}
