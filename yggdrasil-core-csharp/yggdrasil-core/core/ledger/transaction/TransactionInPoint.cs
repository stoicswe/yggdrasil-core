namespace yggdrasil_core.core.ledger.transaction
{
    public class TransactionInPoint : LedgerHashableItem
    {
        private readonly Transaction _txn;
        public Transaction Txn { get { return _txn; } }
        private readonly Coin _value;
        public Coin Value { get { return _value; } }

        public TransactionInPoint(Transaction txn)
        {
            _txn = txn;
            _value = txn.Value();
        }

        public bool IsNull()
        {
            return (_txn == null && _value == null);
        }

        public byte[] Bytes()
        {
            byte[] bytes = new byte[0];
            bytes = (byte[])bytes.Concat(_txn.Bytes());
            bytes = (byte[])bytes.Concat(_value.Bytes());
            return bytes;
        }
    }
}
