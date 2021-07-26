package edu.buet.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.buet.data.TransferOffer;

public class TransferListResponse extends MessageBase<List<TransferOffer>> {
    public TransferListResponse(boolean success, String message, List<TransferOffer> body) {
        super(success, message, body);
    }
    public static TransferListResponse errorMessage(String reason) {
        return new TransferListResponse(false, reason, null);
    }
    public static TransferListResponse successMessage(Collection<TransferOffer> transfers) {
        var r = new ArrayList<TransferOffer>(transfers.size());
        for (var t : transfers)
            r.add(t);
        return new TransferListResponse(true, null, r);
    }
}
