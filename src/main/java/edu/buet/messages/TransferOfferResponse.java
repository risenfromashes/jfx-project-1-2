package edu.buet.messages;

public class TransferOfferResponse extends MessageBase<Void>{

    private TransferOfferResponse(boolean success, String message){
        super(success, message, null);
    }

    public static TransferOfferResponse errorMessage(String reason){
        return new TransferOfferResponse(false, reason);
    }
    
    public static TransferOfferResponse successMessage(){
        return new TransferOfferResponse(true, null);
    }
}
