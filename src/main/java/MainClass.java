import java.security.Security;

public class MainClass {

    public static void main(String[] args) {
        //add our blocks to the blockchain ArrayList:
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider

        //Create wallets:
        Wallet walletA = WalletFactory.create();
        Wallet walletB = WalletFactory.create();

        // fills up walletA's pocket first
        System.out.println("\nSend funds to WalletA: " + walletA.getBalance());
        Block blockInit = new Block(JavaChain.getInstance().getLastBlockHash());
        blockInit.addTransaction(JavaChain.getInstance().getGenesisWallet().sendFunds(walletA.getPublicKey(), 100f));
        JavaChain.getInstance().addBlock(blockInit);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        // add transactions and blocks to the chain
        Block block1 = new Block(JavaChain.getInstance().getLastBlockHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        JavaChain.getInstance().addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(JavaChain.getInstance().getLastBlockHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        JavaChain.getInstance().addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(JavaChain.getInstance().getLastBlockHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.getPublicKey(), 20));
        JavaChain.getInstance().addBlock(block3);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        JavaChain.getInstance().isValid();
    }
}
