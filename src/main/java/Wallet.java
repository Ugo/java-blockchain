import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    // TODO: this field should be private
    public PrivateKey privateKey;
    private PublicKey publicKey;

    //only UTXOs owned by this wallet.
    private HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    Wallet(KeyPair key) {
        this.privateKey = key.getPrivate();
        this.publicKey = key.getPublic();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    //returns balance and stores the UTXO's owned by this wallet in this.UTXOs
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : JavaChain.getInstance().getUTXOs().entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.belongsTo(publicKey)) {
                UTXOs.put(UTXO.hash, UTXO);
                total += UTXO.value;
            }
        }
        return total;
    }

    //Generates and returns a new transaction from this wallet.
    public Transaction sendFunds(PublicKey _recipient, float value) {
        if (getBalance() < value) { //gather balance and check funds.
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        //create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.hash));
            if (total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }
}
