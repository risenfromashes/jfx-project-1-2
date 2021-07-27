package edu.buet.data;

import java.io.Serializable;
import java.util.Map;

public class TransferOffer implements Serializable{
    private final Player player;
    private final Currency fee;
    public TransferOffer(Player player, Currency fee){
        this.player = player;
        this.fee = fee;
    }
    public TransferOffer(String line, Map<Integer, Player> players){
        var props = line.strip().split(";");
        this.player = players.get(Integer.parseInt(props[0]));  
        this.fee = new Currency(props[1]);
    }
    @Override
    public String toString(){
        var sb = new StringBuilder();
        sb.append(player.getId());
        sb.append(';');
        sb.append(fee.getString());
        sb.append(';');
        return sb.toString();
    }
    final public Player getPlayer(){
        return this.player;
    }
    final public Currency getFee(){
        return this.fee;
    }
}
