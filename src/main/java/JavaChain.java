import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class JavaChain {

    private static JavaChain instance = null;

    private static ArrayList<Block> blockchain = new ArrayList<>();
    private static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    private static int difficulty = 5;
    private static float minimumTransaction = 0.1f;
    private static Transaction genesisTransaction;
    private static Wallet genesisWallet;

    private JavaChain(){
        // build the genesis transaction of the chain
        genesisWallet = WalletFactory.create();

        genesisTransaction = new Transaction(genesisWallet.getPublicKey(), genesisWallet.getPublicKey(), 100f, null);
        genesisTransaction.generateSignature(genesisWallet.privateKey);     //manually sign the genesis transaction
        genesisTransaction.transactionId = "0"; //manually set the transaction hash
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
        UTXOs.put(genesisTransaction.outputs.get(0).hash, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);
    }

    public static JavaChain getInstance(){
        if (instance == null) instance = new JavaChain();

        return instance;
    }

    public Wallet getGenesisWallet(){
        return genesisWallet;
    }

    public String getLastBlockHash(){
        if (blockchain.size() > 0){
            return blockchain.get(blockchain.size() - 1).getHash();
        } else {
            return "";
        }
    }
    public float getMinimumTransaction(){
        return minimumTransaction;
    }

    public HashMap<String, TransactionOutput> getUTXOs(){
        return UTXOs;
    }

    public static Boolean isValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).hash, genesisTransaction.outputs.get(0));

        //loop through blockchain to check hashes:
        for (int iterBlock = 1; iterBlock < blockchain.size(); iterBlock++) {

            currentBlock = blockchain.get(iterBlock);
            previousBlock = blockchain.get(iterBlock - 1);

            //compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for (int iterTransaction = 0; iterTransaction < currentBlock.getTransactions().size(); iterTransaction++) {
                Transaction currentTransaction = currentBlock.getTransactions().get(iterTransaction);

                if (!currentTransaction.verifiySignature()) {
                    System.out.println("#Signature on Transaction(" + iterTransaction + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + iterTransaction + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + iterTransaction + ") is Missing");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + iterTransaction + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.hash, output);
                }

                if (currentTransaction.outputs.get(0).recipient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + iterTransaction + ") output reciepient is not who it should be");
                    return false;
                }
                if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + iterTransaction + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public void addBlock(Block newBlock) {
        newBlock.mine(difficulty);
        blockchain.add(newBlock);
        System.out.println("#A new block has been added to the chain");
    }
}