package edu.buet.messages;

public class TransferResponse extends MessageBase<Void> {
    private TransferResponse(boolean success, String message) {
        super(success, message, null);
    }
    public static TransferResponse errorMessage(String reason) {
        return new TransferResponse(false, reason );
    }
    public static TransferResponse successMessage() {
        return new TransferResponse(true, null);
    }
}
