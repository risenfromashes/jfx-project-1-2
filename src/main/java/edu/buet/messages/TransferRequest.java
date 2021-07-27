package edu.buet.messages;

public class TransferRequest extends MessageBase<Integer> {
    public TransferRequest(int playerId ) {
        super(true, null, playerId);
    }
}
