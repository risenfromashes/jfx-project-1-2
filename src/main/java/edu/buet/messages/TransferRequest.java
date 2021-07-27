package edu.buet.messages;

public class TransferRequest extends MessageBase<Integer> {
    private TransferRequest(int playerId ) {
        super(true, null, playerId);
    }
}
