package edu.buet.messages;

import edu.buet.data.TransferOffer;

public class NotifyTransfer extends MessageBase<TransferOffer> {
    public enum Op { ADD, REMOVE }
    public final Op op;
    public final float balanceDelta;
    public NotifyTransfer(Op op, TransferOffer offer, float balanceDelta) {
        super(true, null, offer);
        this.op = op;
        this.balanceDelta = balanceDelta;
    }
    public static NotifyTransfer addMessage(TransferOffer offer) {
        return new NotifyTransfer(Op.ADD, offer, 0.f );
    }
    public static NotifyTransfer removeMessage(TransferOffer offer) {
        return new NotifyTransfer(Op.REMOVE, offer, 0.f);
    }
    public static NotifyTransfer addMessage(TransferOffer offer, float delta) {
        return new NotifyTransfer(Op.ADD, offer, delta);
    }
    public static NotifyTransfer removeMessage(TransferOffer offer, float delta) {
        return new NotifyTransfer(Op.REMOVE, offer, delta);
    }
}
