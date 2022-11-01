using yggdrasil_core.core.utils;

namespace yggdrasil_core.core.ledger
{
    public class Coin : LedgerHashableItem
    {
        public static readonly ulong COIN = 100000000;
        public static readonly ulong CENT = 1000000;

        private readonly ulong _whole;
        public ulong Whole { get { return _whole; } }

        private readonly ulong _partial;
        public ulong Partial { get { return _partial; } }

        public Coin(ulong whole, ulong partial)
        {
            if (whole > COIN) throw new Exception();
            if (partial > CENT) throw new Exception();
            _whole = whole;
            _partial = partial;
        }

        public byte[] Bytes()
        {
            using (MemoryStream ms = new MemoryStream())
            {
                using (BinaryWriter bw = new BinaryWriter(ms))
                {
                    bw.Write(_whole);
                    bw.Write(_partial);
                }
                byte[] bytes = ms.ToArray();
                return bytes;
            }
        }
    }
}
