package edu.buet.messages;

public class NotifyTransfer extends MessageBase<Integer> {
    public NotifyTransfer(int transferId) {
        super(true, null, transferId);
    }
}
