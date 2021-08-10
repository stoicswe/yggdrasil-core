module Txn where

  -- Module: Txn
  -- Author: Nathaniel Bunch
  -- Description: This module is the home of the
  --              different datatypes and functions
  --              related to transactions.

  -- Transactions are made of the following:
  -- Origin address
  -- Origin public key
  -- Destination
  -- TxnInputs (pointing to old TxnOutputs)
  -- TxnOutputs (pointing to where coin should go)
  -- Signature (signature of the origin)
  -- Hash (hash of this txn)
  data Txn = {
    _timestamp :: Integer
    _originAddress :: String -- Hash of public key origin
    _origin :: [Byte] -- Public key of origin address
    _destination :: String -- Hash of publoc key for destination
    _inputs :: [TxnInput]
    _outputs :: [TxnOutput]
    _sig :: [Byte]
    _hash :: [Byte]
  }

  -- TxnInput should reference a previous output
  data TxnInput = {}

  -- The output of a txn, which shows whwre coin is
  -- destined
  data TxnOutput = {}
