package minichain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The MiniChain class represents the main class for the blockchain
 * implementation. It manages the blockchain, wallets, and transactions, and
 * includes methods for validating the chain and adding new blocks.
 * 
 * Author: Sachin Rathod
 */
public class MiniChain {

	// The blockchain is represented as an ArrayList of Block objects
	public static ArrayList<Block> blockChain = new ArrayList<Block>();

	// A HashMap to keep track of all unspent transaction outputs
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	public static int difficulty = 2;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction; // first transaction in the blockchain

	public static void main(String[] args) {

		// Add Bouncy Castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		walletA = new Wallet();
		walletB = new Wallet(); 
		Wallet coinbase = new Wallet();

		// Create the genesis transaction, which sends 100 coins from coinbase to
		// walletA
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinbase.privateKey);
		genesisTransaction.transactionId = "0";
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value,
				genesisTransaction.transactionId));

		// It's important to store our first transaction in the UTXOs list.
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);

		// Test the blockchain with various transactions
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (15) to WalletA...");
		block3.addTransaction(walletB.sendFunds(walletA.publicKey, 15f));
		addBlock(block3);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());


		// Print updated balances after the transaction
		System.out.println("\nWalletA's balance is now: " + walletA.getBalance());
		System.out.println("WalletB's balance is now: " + walletB.getBalance());

		isChainValid();
	}

	// Validates the blockchain by checking the hashes and transaction integrity.
	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		for (int i = 1; i < blockChain.size(); i++) {

			currentBlock = blockChain.get(i);
			previousBlock = blockChain.get(i - 1);

			// Compare registered hash and calculated hash
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("#Current Hashes not equal");
				return false;
			}

			// Compare previous hash and registered previous hash
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("#Previous Hashes not equal");
				System.out.println("Block " + i + ":");
				System.out.println("Previous Block Hash: " + previousBlock.hash);
				System.out.println("Current Block's Previous Hash: " + currentBlock.previousHash);
				return false;
			}

			// Check if hash is solved
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}

			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);

				if (!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false;
				}
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false;
				}

				for (TransactionInput input : currentTransaction.inputs) {
					tempOutput = tempUTXOs.get(input.transactionOutputId);

					if (tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}

					if (input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.transactionOutputId);
				}

				for (TransactionOutput output : currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}

				if (currentTransaction.outputs.get(0).reciepient != currentTransaction.recipient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}

			}

		}
		System.out.println("Blockchain is valid");
		return true;
	}

	// Adds a new block to the blockchain after mining it.
	public static void addBlock(Block newBlock) {

		newBlock.mineBlock(difficulty);
		System.out.println("New Block Created. Previous Hash: " + newBlock.previousHash);
		blockChain.add(newBlock);
	}
}

/*
 * public static void main(String[] args) { //add our blocks to the blockchain
 * ArrayList: Security.addProvider(new
 * org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle
 * as a Security Provider
 * 
 * //walletA = new Wallet(); //walletB = new Wallet();
 * 
 * //System.out.println("Private and public keys:");
 * //System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
 * //System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
 * 
 * createGenesis();
 * 
 * //Transaction transaction = new Transaction(walletA.publicKey,
 * walletB.publicKey, 5); //transaction.signature =
 * transaction.generateSignature(walletA.privateKey);
 * 
 * //System.out.println("Is signature verified:");
 * //System.out.println(transaction.verifiySignature());
 * 
 * }
 */