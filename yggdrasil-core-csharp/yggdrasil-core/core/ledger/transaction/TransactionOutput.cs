using System.Security.Cryptography;

using yggdrasil_core.core.utils;

namespace yggdrasil_core.core.ledger.transaction
{
    public class TransactionOutput : LedgerHashableItem
    {
        private readonly byte[] _address;
        public byte[] Address { get { return _address; } }

        private readonly ulong _value;
        public ulong Value { get { return _value; } }

        public TransactionOutput(byte[] address, ulong value)
        {
            this._address = address;
            this._value = value;
        }

        public override string ToString()
        {
            return String.Format("TxOut(val={0}, address={1})", _value, CryptoHasher.ToHumanReadableHash(_address));
        }

        public bool IsMine(ECDiffieHellmanPublicKey publicKey, byte[] signature)
        {
            var verifier = ECDsa.Create(publicKey.ExportParameters());
            return verifier.VerifyHash(new byte[0], signature);
        }

        public byte[] Bytes()
        {
            byte[] bytes = new byte[0];
            bytes = (byte[])bytes.Concat(_address);
            
            using (MemoryStream ms = new MemoryStream())
            {
                using (BinaryWriter bw = new BinaryWriter(ms))
                {
                    bw.Write(_value);
                }
                bytes = (byte[])bytes.Concat(ms.ToArray());
                return bytes;
            }
        }
    }
}
