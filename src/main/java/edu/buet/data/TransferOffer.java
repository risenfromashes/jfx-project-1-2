package edu.buet.data;

import java.io.Serializable;

public class TransferOffer implements Serializable {
    private final int playerId;
    private final int sellingClubId;
    private final Currency fee;
    public TransferOffer(int playerId, int sellingClubId, Currency fee) {
        this.playerId = playerId;
        this.sellingClubId = sellingClubId;
        this.fee = fee;
    }
    public TransferOffer(String line) {
        var props = line.strip().split(";");
        this.playerId = Integer.parseInt(props[0]);
        this.sellingClubId = Integer.parseInt(props[1]);
        this.fee = new Currency(props[2]);
    }
    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(playerId);
        sb.append(';');
        sb.append(sellingClubId);
        sb.append(';');
        sb.append(fee.getString());
        sb.append(';');
        return sb.toString();
    }
    final public int getPlayerId() {
        return this.playerId;
    }
    final public Currency getFee() {
        return this.fee;
    }
    final public int getSellingClubId(){
        return this.sellingClubId;
    }
}
