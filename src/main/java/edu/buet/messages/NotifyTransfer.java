package edu.buet.messages;

import edu.buet.data.Player;

public class NotifyTransfer extends MessageBase<Player> {
    public enum Op { ADD, REMOVE }
    public final Op op;
    public final float balanceDelta;
    public NotifyTransfer(Op op, Player player, float balanceDelta) {
        super(true, null, player);
        this.op = op;
        this.balanceDelta = balanceDelta;
    }
    public static NotifyTransfer addMessage(Player player, float delta) {
        return new NotifyTransfer(Op.ADD, player, delta);
    }
    public static NotifyTransfer removeMessage(Player player, float delta) {
        return new NotifyTransfer(Op.REMOVE, player, delta);
    }
}
