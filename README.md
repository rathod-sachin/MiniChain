# minichain
Java blockchain implementation for learning purposes.

## Overview

**minichain** is a Java blockchain implementation designed for educational purposes. This project explores the fundamental concepts of blockchain technology through a simplified implementation in Java. It includes features such as block creation, transaction handling, wallet management, and basic mining capabilities.


## Installation

To get started with **minichain**, follow these steps:

1. **Clone the repository:**
   ```sh
   git clone https://github.com/your-username/minichain.git


## Main Method Overview

The `main` method in `MiniChain.java` serves as the entry point for demonstrating the functionality of **minichain**. Here's a breakdown of what it does:

- **Bouncy Castle Provider:** Adds Bouncy Castle as a security provider for cryptographic operations.
- **Wallet Initialization:** Creates three wallets (`walletA`, `walletB`, and `coinbase`) using the `Wallet` class.
- **Genesis Transaction:** Initializes the genesis transaction, which sends 100 coins from the `coinbase` wallet to `walletA`.
- **Genesis Block Creation:** Creates and mines the genesis block, adding the genesis transaction to the blockchain.
- **Testing Transactions:** Demonstrates various transactions between `walletA` and `walletB` across multiple blocks.
- **Validation:** Checks the validity of the blockchain after each transaction.

This method sets up the initial conditions of the blockchain and tests its core functionalities such as transaction processing and blockchain validation.
