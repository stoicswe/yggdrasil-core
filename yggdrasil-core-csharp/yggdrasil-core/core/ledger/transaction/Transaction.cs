using System.Security.Cryptography;

using yggdrasil_core.core.utils;

namespace yggdrasil_core.core.ledger.transaction
{
    public class Transaction : LedgerHashableItem
    {
        private readonly DateTime _timestamp;
        public DateTime Timestamp { get { return _timestamp; } }

        private readonly string _originAddress;
        public string OriginAddress { get { return _originAddress; } }

        private readonly ECDiffieHellmanPublicKey _publicKey;
        public ECDiffieHellmanPublicKey PublicKey { get { return _publicKey; } }

        private readonly String _destinationAddress;
        public string DestinationAddress { get { return _destinationAddress; } }

        private readonly TransactionInput[] _txnInputs;
        public TransactionInput[] TxInputs { get { return _txnInputs; } }

        private readonly TransactionOutput[] _txnOutputs;
        public TransactionOutput[] TxOutputs { get { return _txnOutputs; } }

        private readonly byte[] _signature;
        public byte[] Signature { get { return _signature; } }

        private byte[] _txnHash;
        public byte[] TxHash { get { return _txnHash; } }

        public Transaction(TransactionFactory factory)
        {
            this._timestamp = factory._timestamp;
            this._originAddress = factory._originAddress;
            this._publicKey = factory._publicKey;
            this._destinationAddress = factory._destinationAddress;
            this._txnInputs = factory._txnInputs;
            this._txnOutputs = factory._txnOutputs;
            this._txnHash = CryptoHasher.Hash(this);
        }

        public Coin Value()
        {
            // computation is the summation of the inputs, minus the outputs
            /*
             *  BigDecimal val = BigDecimal.ZERO;
                for(TransactionInput txnIn: txnInputs) {
                    val = val.add(txnIn.value);
                }
                for(TransactionOutput txnOut: txnOutPuts) {
                    val = val.subtract(txnOut.value);
                }
             */
            return null;
        }

        public byte[] Rehash()
        {
            this._txnHash = CryptoHasher.Hash(this);
            return this._txnHash;
        }

        public bool IsCoinbase()
        {
            return (this._txnInputs.Length == 1 && this._txnInputs[0].TxOutPt == null);
        }

        public override string ToString()
        {
            return CryptoHasher.ToHumanReadableHash(_txnHash);
        }

        public byte[] Bytes()
        {
            using (MemoryStream ms = new MemoryStream())
            {
                using (BinaryWriter bw = new BinaryWriter(ms))
                {
                    bw.Write((DateTimeUtil.toEpockSecondTimeStamp(_timestamp)));
                    bw.Write(_originAddress);
                    bw.Write(_destinationAddress);
                }
                byte[] bytes = ms.ToArray();
                bytes = (byte[])bytes.Concat(_publicKey.ToByteArray());
                bytes = (byte[])bytes.Concat(_signature);
                foreach (var txIns in _txnInputs)
                {
                    bytes = (byte[])bytes.Concat(txIns.Bytes());
                }
                foreach (var txOuts in _txnOutputs)
                {
                    bytes = (byte[])bytes.Concat(txOuts.Bytes());
                }
                return bytes;
            }
        }

        public bool CompareHash(byte[] otherHash)
        {
            try
            {
                for(var i = 0; i < otherHash.Length; i++)
                {
                    if (this._txnHash[i] != otherHash[i])
                    {
                        return false;
                    }
                }
            } catch (IndexOutOfRangeException e)
            {
                return false;
            }
            return true;
        }
    }

    public class TransactionFactory
    {
        internal DateTime _timestamp;
        public DateTime Timestamp { set { _timestamp = value; } }

        internal string _originAddress;
        public string OriginAddress { set { _originAddress = value; } }

        internal ECDiffieHellmanPublicKey _publicKey;
        public ECDiffieHellmanPublicKey PublicKey { set { _publicKey = value; } }

        internal string _destinationAddress;
        public string DestinationAddress { set { _destinationAddress = value; } }

        internal TransactionInput[] _txnInputs;
        public TransactionInput[] TxnInputs { set { _txnInputs = value; } }

        internal TransactionOutput[] _txnOutputs;
        public TransactionOutput[] TxnOutputs { set { _txnOutputs = value; } }

        public TransactionFactory() { }

        public Transaction Build()
        {
            return new Transaction(this);
        }

        public Transaction BuildFromMessage()
        {
            return null;
        }
    }
}
