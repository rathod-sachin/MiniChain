package minichain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 * The Transaction class represents a transaction in the blockchain.
 */
public class Transaction {

	public String transactionId;
	public PublicKey sender;
	public PublicKey recipient;
	public float value; // amount of coins to be transferred.
	public byte[] signature; // this prevents anyone else from spending funds in our wallet.

	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0; // rough count of how many transactions have been generated.

	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
	}

	// Processes the transaction and checks if it is valid.
	public boolean processTransaction() {

		// Verify the signature of the transaction.
		if (verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		// Gather all input transactions (UTXOs) referenced in this transaction.
		for (TransactionInput i : inputs) {
			i.UTXO = MiniChain.UTXOs.get(i.transactionOutputId);
		}

		// Check if the transaction value is greater than the minimum transaction
		// amount.
		if (getInputsValue() < MiniChain.minimumTransaction) {
			System.out.println("Transaction Inputs too small: " + getInputsValue());
			System.out.println("Please enter the amount greater than " + MiniChain.minimumTransaction);
			return false;
		}

		// Calculate the leftover amount after sending the value to the recipient.
		float leftOver = getInputsValue() - value;

		// Generate a unique transaction ID (hash) for this transaction.
		transactionId = calulateHash();

		// Add transaction outputs (new coins) to the recipient and sender.
		outputs.add(new TransactionOutput(this.recipient, value, transactionId));
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

		// Add new outputs to the list of unspent transaction outputs (UTXOs).
		for (TransactionOutput o : outputs) {
			MiniChain.UTXOs.put(o.id, o);
		}

		// Remove spent outputs from the list of UTXOs.
		for (TransactionInput i : inputs) {
			if (i.UTXO == null)
				continue;
			MiniChain.UTXOs.remove(i.UTXO.id);
		}

		return true;
	}

	// Calculates the total value of the inputs (previous transaction outputs) being
	// spent in this transaction.
	public float getInputsValue() {
		float total = 0;
		for (TransactionInput i : inputs) {
			if (i.UTXO == null)
				continue;
			total += i.UTXO.value;
		}
		return total;
	}

	/**
	 * Generates a digital signature for this transaction using the sender's private
	 * key.
	 * 
	 * @param privateKey The private key of the sender.
	 */
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}

	// Verifies the digital signature of this transaction.
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}

	// Calculates a hash (unique ID) for this transaction.
	private String calulateHash() {
		sequence++;

		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ Float.toString(value) + sequence);
	}
}