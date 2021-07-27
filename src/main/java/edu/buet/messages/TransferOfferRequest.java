package edu.buet.messages;

import java.io.Serializable;

public class TransferOfferRequest extends MessageBase<TransferOfferRequest.Data> {
    public static class Data implements Serializable{
        public final int playerId;
        public final float fee;
        public Data(int playerId, float fee){
            this.playerId = playerId;
            this.fee = fee;
        }
    }
    public TransferOfferRequest(int playerId, float fee){
        super(true, null, new Data(playerId, fee));
    }
}
