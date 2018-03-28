public class TransactionInput {

    // Reference to TransactionOutputs -> transactionId
    public String transactionOutputId;

    // Contains the Unspent transaction output
    public TransactionOutput UTXO;

    TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}