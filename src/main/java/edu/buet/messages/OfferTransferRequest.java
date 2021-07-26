package edu.buet.messages;

import edu.buet.data.Currency;

public class OfferTransferRequest extends MessageBase<OfferTransferRequest.Data> {
    public static class Data{
        final int playerId;
        final float fee;
        public Data(int playerId, float fee){
            this.playerId = playerId;
            this.fee = fee;
        }
    }
    public OfferTransferRequest(int playerId, float fee){
        super(true, null, new Data(playerId, fee));
    }
}
