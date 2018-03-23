import java.security.PublicKey;

public class TransactionOutput {

    // hash of the transaction
    public String hash;
    public PublicKey recipient;
    public float value; //the amount of coins they own
    public String parentTransactionId; //the hash of the transaction this output was created in

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.hash = computeHash();
    }

    private String computeHash(){
        return StringUtil.applySha256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
    }

    //Check if the transaction belongs to a particular public key
    public boolean belongsTo(PublicKey publicKey) {
        return (publicKey == recipient);
    }

}