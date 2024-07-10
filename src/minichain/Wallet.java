package minichain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The Wallet class manages the user's private and public keys and the Unspent
 * Transaction Outputs (UTXOs) belonging to the wallet.
 */
public class Wallet {

	public PrivateKey privateKey;
	public PublicKey publicKey;

	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

	public Wallet() {
		generateKeyPair();
	}

	// Generates a new key pair (private and public key) using ECDSA algorithm and
	// Bouncy Castle provider.
	public void generateKeyPair() {

		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Calculates and returns the balance of the wallet by summing up the values of
	 * all UTXOs belonging to the wallet.
	 * 
	 * @return The total balance of the wallet.
	 */
	public float getBalance() {
		float total = 0;

		for (Map.Entry<String, TransactionOutput> item : MiniChain.UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();

			if (UTXO.isMine(publicKey)) { // If output belongs to this wallet
				UTXOs.put(UTXO.id, UTXO);
				total += UTXO.value;
			}
		}
		return total;
	}

	/**
	 * Creates a new transaction to send funds from this wallet to a recipient's
	 * public key.
	 * 
	 * @param _recipient The public key of the recipient.
	 * @param value      The amount to send.
	 * @return The created transaction or null if there are insufficient funds.
	 */
	public Transaction sendFunds(PublicKey _recipient, float value) {

		if (getBalance() < value) { // Check if there are enough funds
			System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
			return null;
		}

		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

		float total = 0;

		// Gather enough UTXOs to cover the transaction value
		for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if (total > value)
				break;
		}

		Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
		newTransaction.generateSignature(privateKey);

		// Remove the used UTXOs from the wallet's UTXOs
		for (TransactionInput input : inputs) {
			UTXOs.remove(input.transactionOutputId);
		}

		return newTransaction;
	}
}