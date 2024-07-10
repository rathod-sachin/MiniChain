# minichain
Java blockchain implementation for learning purposes.

## Overview

**minichain** is a Java blockchain implementation designed for educational purposes. This project explores the fundamental concepts of blockchain technology through a simplified implementation in Java. It includes features such as block creation, transaction handling, wallet management, and basic mining capabilities.

## Table of Contents

- [Overview](#overview)
- [Installation](#installation)
- [Project Structure](#project-structure)
- [Components Overview](#components-overview)
- [Main Method Overview](#main-method-overview)
- [Usage](#usage)
- [Contact](#contact)

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/rathod-sachin/minichain.git
    ```
2. Open the project in Eclipse IDE.
3. Ensure you have the required dependencies:
    - JDK 21
    - Gson 2.6.2
    - Bouncy Castle 1.78.1
  
## Project Structure

The project consists of the following main components:

- **Block.java**: Represents a block in the blockchain. It includes attributes and methods for block creation, hashing, and mining.
- **Transaction.java**: Manages transactions between wallets. It includes methods for signing and verifying transactions.
- **Wallet.java**: Represents a user's wallet. It includes methods for generating keys, signing transactions, and managing balances.
- **StringUtil.java**: Provides utility methods for hashing and generating Merkle roots.
- **MiniChain.java**: The main class to run and test the blockchain functionality.

## Components Overview

### Block.java
Handles block creation, hashing, and mining in the blockchain.

### Transaction.java
Manages transactions, including signing and verification.

### Wallet.java
Represents a user's wallet and manages keys and balances.

### StringUtil.java
Provides utility methods for cryptographic operations.

### MiniChain.java
The main class to run the blockchain and test its functionality.

## Main Method Overview

The `main` method in `MiniChain.java` serves as the entry point for demonstrating the functionality of **minichain**. Here's a breakdown of what it does:

- **Bouncy Castle Provider:** Adds Bouncy Castle as a security provider for cryptographic operations.
- **Wallet Initialization:** Creates three wallets (`walletA`, `walletB`, and `coinbase`) using the `Wallet` class.
- **Genesis Transaction:** Initializes the genesis transaction, which sends 100 coins from the `coinbase` wallet to `walletA`.
- **Genesis Block Creation:** Creates and mines the genesis block, adding the genesis transaction to the blockchain.
- **Testing Transactions:** Demonstrates various transactions between `walletA` and `walletB` across multiple blocks.
- **Validation:** Checks the validity of the blockchain after each transaction.

This method sets up the initial conditions of the blockchain and tests its core functionalities such as transaction processing and blockchain validation.

## Usage

1. Run the `MiniChain.java` class to start the blockchain simulation.
2. Follow the prompts in the console to observe the blockchain and transaction process.

## Contact

For any questions or inquiries, please contact Sachin Rathod at sachinrathod2906@gmail.com.
