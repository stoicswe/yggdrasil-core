module Yggdrasil.Core.Ledger (LedgerItem) where
  -- Imports
  import Data.Binary
  import Data.ByteString
  import Data.Time
  -- Ledger data items should
  -- be able to have their bytes
  -- retrieved to be hashed
  class LedgerItem li where
    getTimeStamp :: li -> Day
    getHash :: li -> ByteString
    getDataBytes :: li -> ByteString
