package edu.buet.data;

import java.io.Serializable;
import java.util.Map;

public class Player implements Serializable{

    public enum Foot {
        LEFT, RIGHT;
        public static Foot fromString(String str) {
            if (str.toLowerCase().equals("left"))
                return LEFT;
            if (str.toLowerCase().equals("right"))
                return RIGHT;
            throw new RuntimeException("Invalid value for Foot");
        }
    }

    private final int id;
    private final String name;
    private final String altName;
    private final int age;
    private final Position position;
    private final Foot preferredFoot;
    private final Country country;
    private final int height;
    private final int weight;
    private final int jerseyNumber;
    private final Currency weeklySalary;
    private final Currency value;
    private final String imageUrl;
    private Club club;
    private TransferOffer transferOffer;

    public Player(int id,
                  String name,
                  String altName,
                  int age,
                  Position position,
                  Foot preferredFoot,
                  Club club,
                  Country country,
                  int height,
                  int weight,
                  int jerseyNumber,
                  Currency weeklySalary,
                  Currency value,
                  String imageUrl) {
        this.id = id;
        this.name = name;
        this.altName = altName;
        this.age = age;
        this.position = position;
        this.preferredFoot = preferredFoot;
        this.club = club;
        this.country = country;
        this.height = height;
        this.weight = weight;
        this.jerseyNumber = jerseyNumber;
        this.weeklySalary = weeklySalary;
        this.value = value;
        this.imageUrl = imageUrl;
        this.transferOffer = null;
    }

    public Player(String line, Map<Integer, Club> clubs, Map<Integer, Country> countries) {
        var props = line.strip().split(";");
        this.id = Integer.parseInt(props[0]);
        this.name = props[1];
        this.altName = props[2];
        this.age = Integer.parseInt(props[3]);
        this.position = new Position(props[4]);
        this.preferredFoot = Foot.fromString( props[5]);
        this.club = clubs.get(Integer.parseInt(props[6]));
        this.country = countries.get(Integer.parseInt(props[7]));
        this.height = Integer.parseInt(props[8]);
        this.weight = Integer.parseInt(props[9]);
        this.jerseyNumber = Integer.parseInt(props[10]);
        this.weeklySalary = new Currency(props[11]);
        this.value = new Currency(props[12]);
        this.imageUrl = props[13];
    }

    public String toString() {
//id;name;alt-name;age;position;preferred-foot;club-id;country-id;height;weight;number;salary;value;image-link;
        var sb = new StringBuilder();
        sb.append(id);
        sb.append(';');
        sb.append(name);
        sb.append(';');
        sb.append(altName);
        sb.append(';');
        sb.append(age);
        sb.append(';');
        sb.append(position.getShortPosition());
        sb.append(';');
        sb.append(preferredFoot.toString());
        sb.append(';');
        sb.append(club.getId());
        sb.append(';');
        sb.append(country.getId());
        sb.append(';');
        sb.append(height);
        sb.append(';');
        sb.append(weight);
        sb.append(';');
        sb.append(jerseyNumber);
        sb.append(';');
        sb.append(weeklySalary.getString());
        sb.append(';');
        sb.append(value.getString());
        sb.append(';');
        sb.append(imageUrl);
        sb.append(';');
        return sb.toString();
    }

    final public void setTransferOffer(TransferOffer transfer){
        this.transferOffer = transfer;
    }
    final public TransferOffer getTransferOffer(){
        return this.transferOffer;
    }

    final public int getId() {
        return this.id;
    }
    final public String getName() {
        return this.name;
    }
    final public String getAltName() {
        return this.altName;
    }
    final public int getAge() {
        return this.age;
    }
    final public Position getPosition() {
        return this.position;
    }
    final public Foot getPreferredFoot() {
        return this.preferredFoot;
    }
    final public Club getClub() {
        return this.club;
    }
    final public void setClub(Club club) {
        this.club = club;
    }
    final public Country getCountry() {
        return this.country;
    }
    final public int getHeight() {
        return this.height;
    }
    final public int getWeight() {
        return this.weight;
    }
    final public int getJerseyNumber() {
        return this.jerseyNumber;
    }
    final public Currency getWeeklySalary() {
        return this.weeklySalary;
    }
    final public Currency getValue() {
        return this.value;
    }
    final public String getImageUrl() {
        return this.imageUrl;
    }
}
