package edu.buet.messages;

public class TransferRequest extends MessageBase<Integer>{
    private TransferRequest(int transferId){
        super(true, null, transferId);
    }
}
