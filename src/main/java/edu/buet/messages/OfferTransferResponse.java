package edu.buet.messages;

public class OfferTransferResponse extends MessageBase<Void>{

    private OfferTransferResponse(boolean success, String message){
        super(success, message, null);
    }

    public static OfferTransferResponse errorMessage(String reason){
        return new OfferTransferResponse(false, reason);
    }
    
    public static OfferTransferResponse successMessage(){
        return new OfferTransferResponse(true, null);
    }
}
