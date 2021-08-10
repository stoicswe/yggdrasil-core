module Crypto where

  -- Module: Crypto
  -- Author: Nathaniel Bunch
  -- Desc: This file is the home for functions
  --       related to hashing different pieces of
  --       data and generation of public/private keys
  let _HASH_ALGORITHM = "SHA-256"

  hashLedgerItem :: LedgerItem -> [Byte]
  hashLedgerItem li =
    case li of
      Block b -> getDataBytes b
      Txn t -> getDataBytes t
      BTxn bt -> getDataBytes bt
