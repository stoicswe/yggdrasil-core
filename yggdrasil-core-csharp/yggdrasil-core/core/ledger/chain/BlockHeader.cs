using System.Linq;

using yggdrasil_core.core.utils;

namespace yggdrasil_core.core.ledger.chain
{
    public class BlockHeader : LedgerHashableItem
    {
        private readonly int _version;
        public int Version { get { return _version; } }

        private readonly byte[] _previousBlockHash;
        public byte[] PreviousBlockHash { get { return _previousBlockHash; } }

        private byte[] _merkleRoot;
        public byte[] MerkleRoot { set { _merkleRoot = value; } get { return _merkleRoot; } }

        private DateTime _time;
        public DateTime Time { set { _time = value; } get { return _time; } }

        private readonly int _diff;
        public int Diff { get { return _diff; } }

        private readonly int _nonce;
        public int Nonce { get { return _nonce; } }

        internal BlockHeader(BlockHeaderFactory factory) 
        {
            this._version = factory._version;
            this._previousBlockHash = factory._previousBlockHash;
            this._merkleRoot = factory._merkleRoot;
            this._time = factory._time;
            this._diff = factory._diff;
            this._nonce = factory._nonce;
        }

        public byte[] Bytes()
        {
            using(MemoryStream ms = new MemoryStream())
            {
                using(BinaryWriter bw = new BinaryWriter(ms))
                {
                    bw.Write(_version);
                    bw.Write(_previousBlockHash);
                    bw.Write(_merkleRoot);
                    bw.Write(DateTimeUtil.toEpockSecondTimeStamp(_time));
                    bw.Write(_diff);
                    bw.Write(_nonce);
                }
                return ms.ToArray();
            }
        } 
    }

    public class BlockHeaderFactory
    {
        internal int _version;
        public int Version { set => _version = value;}

        internal byte[] _previousBlockHash;
        public byte[] PreviousBlockHash { set => _previousBlockHash = value; }

        internal byte[] _merkleRoot;
        public byte[] MerkleRoot { set => _merkleRoot = value; }

        internal DateTime _time;
        public DateTime DateTime { set => _time = value; }

        internal int _diff;
        public int Diff { set => _diff = value; }

        internal int _nonce;
        public int Nonce { set => _nonce = value; }

        public BlockHeaderFactory() { }

        public BlockHeader Build()
        {
            return new BlockHeader(this);
        }
    }
}
