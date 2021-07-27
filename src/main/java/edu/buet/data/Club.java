package edu.buet.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Club implements Serializable{
//id;name;value;balance;logo-link;
    private final int id;
    private final String name;
    private final String altName;
    private final Currency value;
    private final String logoUrl;
    private final List<Player> players;

    private Currency balance;

    public Club(int id, String name, String altName, Currency value, Currency balance, String logoUrl){
        this.id = id;
        this.name = name;
        this.altName = altName;
        this.value = value;
        this.balance = balance;
        this.logoUrl = logoUrl;
        this.players = new ArrayList<>();
    }
    public Club(String line){
        var props = line.strip().split(";");
        this.id = Integer.parseInt(props[0]);
        this.name = props[1];
        this.altName = props[2];
        this.value = new Currency(props[3]);
        this.balance = new Currency(props[4]);
        this.logoUrl = props[5];
        this.players = new ArrayList<>();
    }
    final public List<Player> getPlayers(){
        return this.players;
    }
    final public void addPlayer(Player p){
        players.add(p);
    }
    final public void removePlayer(Player p){
        players.remove(p);
    }
    final public int getId(){
        return id;
    }
    final public String getName(){
        return name;
    }
    final public String getAltName(){
        return altName;
    }
    final public Currency getValue(){
        return value;
    }
    final public Currency getBalance(){
        return balance;
    }
    final public String getLogoUrl(){
        return logoUrl;
    }
    public String toString() {
//id;name;alt-name;value;balance;logo-link;
        var sb = new StringBuilder();
        sb.append(id);
        sb.append(';');
        sb.append(name);
        sb.append(';');
        sb.append(altName);
        sb.append(';');
        sb.append(value.getString());
        sb.append(';');
        sb.append(balance.getString());
        sb.append(';');
        sb.append(logoUrl);
        sb.append(';');
        return sb.toString();
    }
    @Override
    public boolean equals(Object other){
        return id == ((Club)other).id;
    }
}
