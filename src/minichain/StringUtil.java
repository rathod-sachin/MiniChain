package minichain;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.gson.GsonBuilder;

/**
 * The StringUtil class provides utility methods for cryptographic operations,
 * JSON serialization, and Merkle tree calculations.
 */
public class StringUtil {

	/**
	 * Applies the SHA-256 hashing algorithm to a given input string.
	 * 
	 * @param input The input string to be hashed
	 * @return The hashed string in hexadecimal format
	 */
	public static String applySha256(String input) {

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			// Applies sha256 to our input,
			byte[] hash = digest.digest(input.getBytes("UTF-8"));

			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Applies the ECDSA Signature algorithm and returns the signature as bytes.
	 * 
	 * @param privateKey The private key used for signing
	 * @param input      The input data to be signed
	 * @return The signature as bytes
	 */
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}

	/**
	 * Verifies an ECDSA(String) signature against a public key and input data.
	 * 
	 * @param publicKey The public key used for verification
	 * @param data      The input data that was signed
	 * @param signature The signature to be verified
	 * @return true if the signature is valid, false otherwise
	 */
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Short hand helper to turn Object into a json string
	public static String getJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}

	// Returns difficulty string target, to compare to hash. e.g difficulty of 5
	// will return "00000"
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}

	// Converts a cryptographic key into a Base64-encoded string.
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	/**
	 * Computes the Merkle root of a list of transactions using SHA-256 hashing.
	 * 
	 * @param transactions The list of transactions to be included in the Merkle
	 *                     tree
	 * @return The Merkle root as a hashed string
	 */
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();

		List<String> previousTreeLayer = new ArrayList<String>();
		for (Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.transactionId);
		}
		List<String> treeLayer = previousTreeLayer;

		while (count > 1) {
			treeLayer = new ArrayList<String>();
			for (int i = 1; i < previousTreeLayer.size(); i += 2) {
				treeLayer.add(applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}

		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}
}