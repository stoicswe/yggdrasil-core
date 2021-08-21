module Yggdrasil.Core.Crypto (hashLedgerItem) where
  -- base imports
  import Data.Binary
  import Data.ByteString
  -- yggdrasil imports
  import Yggdrasil.Core.Ledger.LedgerItem

  -- Module: Crypto
  -- Author: Nathaniel Bunch
  -- Desc: This file is the home for functions
  --       related to hashing different pieces of
  --       data and generation of public/private keys
  _HASH_ALGORITHM = "SHA-256"

  hashLedgerItem :: LedgerItem -> ByteString
  hashLedgerItem li =
    case li of
      Block b -> getDataBytes b
      Txn t -> getDataBytes t
      BTxn bt -> getDataBytes bt
