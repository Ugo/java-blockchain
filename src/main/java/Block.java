import java.util.ArrayList;
import java.util.Date;

public class Block {

    private String hash;
    private String previousHash;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private long timeStamp;
    private int nonce;

    Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    //Calculate new hash based on blocks contents
    public String calculateHash() {
        return StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        nonce +
                        StringUtil.getMerkleRoot(transactions)
        );
    }

    //Increases nonce value until hash target is reached.
    public void mine(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        hash = calculateHash();
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }


    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if (transaction == null) return false;
        if (!previousHash.equals("0")) {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

}