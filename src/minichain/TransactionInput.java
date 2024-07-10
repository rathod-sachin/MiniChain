package minichain;

/**
 * The TransactionInput class represents an input to a transaction, referring to
 * a previous transaction output (UTXO).
 */
public class TransactionInput {

	public String transactionOutputId; // Reference to TransactionOutputs -> transactionId
	public TransactionOutput UTXO; // Contains the unspent transaction output

	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}