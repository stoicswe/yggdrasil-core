namespace yggdrasil_core.core.ledger.transaction
{
    public class TransactionInput : LedgerHashableItem
    {
        private readonly TransactionOutPoint _txOutPt;
        public TransactionOutPoint TxOutPt { get { return _txOutPt; } }

        private readonly Coin _value;
        public Coin Value { get { return _value; } }

        public TransactionInput(TransactionOutPoint txOutPt, Coin value)
        {
            this._txOutPt = txOutPt;
            this._value = value;
        }

        public TransactionInput(byte[] prevBlkHash, byte[] prevTxHash, Coin valueOut)
        {
            this._txOutPt = new TransactionOutPoint(prevTxHash, prevTxHash, valueOut);
            this._value = valueOut;
        }

        public byte[] Bytes()
        {
            byte[] bytes = new byte[0];
            bytes = (byte[])bytes.Concat(_txOutPt.Bytes());
            bytes = (byte[])bytes.Concat(_value.Bytes());
            return bytes;
        }
    }
}
