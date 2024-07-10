package minichain;

import java.util.ArrayList;
import java.util.Date;

/**
 * The Block class represents a single block in the blockChain. Each block
 * contains a hash, the hash of the previous block, a Merkle root, a list of
 * transactions, a timestamp, and a nonce for mining purposes.
 */
public class Block {

	public String hash;
	public String previousHash;
	public String merkleRoot; // The Merkle root of the transactions in this block
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); // The list of transactions in this block
	private long timeStamp; // The timestamp of when this block was created
	private int nonce; // The nonce used for mining this block

	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();

		this.hash = calculateHash();
	}

	// Calculates the hash for this block.
	public String calculateHash() {
		String calculatedhash = StringUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);

		return calculatedhash;
	} 

	// Mines the block by finding a hash that starts with a certain number of zeros
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDificultyString(difficulty);
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}

	// Adds a transaction to this block.
	public boolean addTransaction(Transaction transaction) {

		if (transaction == null)
			return false;

		// If the block is not the genesis block, process the transaction
		if ((!"0".equals(previousHash))) {
			if ((transaction.processTransaction() != true)) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}

		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");

		return true;
	}
}