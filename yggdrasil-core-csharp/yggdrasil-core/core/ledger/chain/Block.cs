using System;
using yggdrasil_core.core.ledger.transaction;
using yggdrasil_core.core.utils;

namespace yggdrasil_core.core.ledger.chain
{
    public class Block : LedgerHashableItem
    {
        private readonly BlockHeader _header;
        public BlockHeader Header { get { return _header; } }

        private readonly ulong _blockHeight;
        public ulong BlockHeight { get { return _blockHeight; } }

        private readonly Transaction[] _data;
        public Transaction[] Data { get { return _data; } } 

        private byte[] _blockHash;
        public byte[] BlockHash { get { return _blockHash; } }

        internal Block(BlockFactory factory)
        {
            this._header = factory._header;
            this._blockHeight = factory._blockHeight;
            this._data = factory._data;
            this._blockHash = CryptoHasher.Hash(_header);
        }

        public Transaction FindTransaction(byte[] txnHash)
        {
            return this._data.Where(txn => txn.CompareHash(txnHash)).ToList().First();
        }

        public int TxnCount()
        {
            return (_data != null) ? _data.Length : -1;
        }

        public bool CompareHash(byte[] otherHash)
        {
            try
            {
                for(var i = 0; i < _blockHash.Length; i++)
                {
                    if (otherHash[i] != _blockHash[i]) return false;
                }
            } catch (IndexOutOfRangeException e)
            {
                return false;
            }
            return true;
        }

        override public string ToString()
        {
            return CryptoHasher.ToHumanReadableHash(_blockHash);
        }

        public byte[] Bytes()
        {
            using (MemoryStream ms = new MemoryStream())
            {
                using (BinaryWriter bw = new BinaryWriter(ms))
                {
                    bw.Write(_blockHash);
                    bw.Write(_blockHeight);
                }
                byte[] bytes = ms.ToArray();
                foreach (var txn in _data)
                {
                    bytes = (byte[])bytes.Concat(txn.Bytes());
                }
                return (byte[])bytes.Concat(_header.Bytes());
            }
        }

        public static Block Genesis()
        {
            //NOT IMPLEMENTED
            return null;
        }
    }

    public class BlockFactory
    {
        internal BlockHeader _header;
        public BlockHeader Header { set { _header = value; } }

        internal ulong _blockHeight;
        public ulong BlockHeight { set { _blockHeight = value; } }

        internal Transaction[] _data;
        public Transaction[] Data { set { _data = value; } }

        public BlockFactory() { }

        public Block Build()
        {
            return new Block(this);
        }

        public Block BuildFromHeaderMessage()
        {
            // NOT IMPLEMENTED
            return null;
        }

        public Block BuildFromBlockMessage()
        {
            //NOT IMPLEMENTED
            return null;
        }
    }
}
