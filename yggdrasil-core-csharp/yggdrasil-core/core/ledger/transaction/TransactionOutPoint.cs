using System.Linq;

namespace yggdrasil_core.core.ledger.transaction
{
    public class TransactionOutPoint : LedgerHashableItem
    {
        private readonly byte[] _blockHash;
        public byte[] BlockHash { get { return _blockHash; } }

        private readonly byte[] _txnHash;
        public byte[] TxnHash { get { return _txnHash; } }

        private readonly Coin _value;
        public Coin Value { get { return _value; } }

        public TransactionOutPoint()
        {
            _blockHash = new byte[0];
            _txnHash = new byte[0];
            _value = new Coin(0,0);
        }

        public TransactionOutPoint(byte[] blockHash, byte[] txnHash, Coin value)
        {
            _blockHash = blockHash;
            _txnHash = txnHash;
            _value = value;
        }

        public byte[] Bytes()
        {
            byte[] bytes = new byte[0];
            bytes = (byte[])bytes.Concat(_blockHash);
            bytes = (byte[])bytes.Concat(_txnHash);
            bytes = (byte[])bytes.Concat(_value.Bytes());
            return bytes;
        }
    }
}
