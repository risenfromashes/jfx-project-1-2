package edu.buet.messages;

import java.util.List;

import edu.buet.data.Player;

public class TransferListResponse extends MessageBase<List<Player>> {
    public TransferListResponse(boolean success, String message, List<Player> body) {
        super(success, message, body);
    }
    public static TransferListResponse errorMessage(String reason) {
        return new TransferListResponse(false, reason, null);
    }
    public static TransferListResponse successMessage(List<Player> transfers) {
        return new TransferListResponse(true, null, transfers);
    }
}
