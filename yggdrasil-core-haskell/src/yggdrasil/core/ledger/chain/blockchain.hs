module Yggdrasil.Core.Ledger.Chain.Blockchain where

  -- Module: Blockchain
  -- Author: Nathaniel Bunch
  -- Desc: This file is the home of the different
  --       data types and functions that make up the
  --       blockchain.

  -- Make these LedgerItems able to be exported to
  -- [Byte] for hashing.
  data Blockchain = {
    _blockSolveWindow :: Integer
    _baseDifficulty :: Integer
    _blocks :: [Block]
    _lastBlockHash :: [Byte]
  }

  data Block = {
    _height :: Integer
    _timeStamp :: Integer
    _data :: [Txn]
    _merkleRoot :: [Byte]
    _previousHash :: [Byte]
    _blockHash :: [Byte]
    _signature :: [Byte]
    _nonce :: Integer
  }
