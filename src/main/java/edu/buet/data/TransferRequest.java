package edu.buet.data;

import java.util.Map;

public class TransferRequest{
    private final int id;
    private final Player player;
    private final Currency fee;
    public TransferRequest(int id, Player player, Currency fee){
        this.id = id;
        this.player = player;
        this.fee = fee;
    }
    public TransferRequest(String line, Map<Integer, Player> players){
        var props = line.strip().split(";");
        this.id = Integer.parseInt(props[0]);
        this.player = players.get(Integer.parseInt(props[1]));  
        this.fee = new Currency(props[2]);
    }
    @Override
    public String toString(){
        var sb = new StringBuilder();
        sb.append(id);
        sb.append(';');
        sb.append(player.getId());
        sb.append(';');
        sb.append(fee.getString());
        sb.append(';');
        return sb.toString();
    }
    final public int getId(){
        return this.id;
    }
    final public Player getPlayer(){
        return this.player;
    }
    final public Currency getFee(){
        return this.fee;
    }
}
