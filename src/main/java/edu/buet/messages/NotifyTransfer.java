package edu.buet.messages;

import edu.buet.data.Player;

public class NotifyTransfer extends MessageBase<Player> {
    public enum Op { ADD_TRANSFER, REMOVE_TRANSFER, ADD_PLAYER, REMOVE_PLAYER }
    public final Op op;
    public final float balanceDelta;
    public NotifyTransfer(Op op, Player player, float balanceDelta) {
        super(true, null, player);
        this.op = op;
        this.balanceDelta = balanceDelta;
    }
}
