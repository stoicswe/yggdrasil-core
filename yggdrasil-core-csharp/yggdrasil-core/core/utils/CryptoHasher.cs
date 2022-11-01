using SshNet.Security.Cryptography;
using System.Security.Cryptography;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.Crypto;

using yggdrasil_core.core.ledger;
using yggdrasil_core.core.ledger.transaction;
using SHA256 = System.Security.Cryptography.SHA256;

namespace yggdrasil_core.core.utils
{
    public class CryptoHasher
    {
        /// <summary>
        /// Returns the hash of a ledger item's data.
        /// </summary>
        /// <param name="item"></param>
        /// <returns></returns>
        public static byte[] Hash(LedgerHashableItem item)
        {
            return DHash(item.Bytes());
        }

        /// <summary>
        /// Returns the double SHA256 hash of byte[].
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        public static byte[] DHash(byte[] data)
        {
            byte[] hash;
            using(SHA256 sha256 = SHA256.Create())
            {
                hash = sha256.ComputeHash(sha256.ComputeHash(data));
            }
            return hash;
        }

        /// <summary>
        /// Returns the single SHA256 hash of byte[].
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        public static byte[] SHash(byte[] data)
        {
            byte[] hash;
            using(SHA256 sha256 = SHA256.Create())
            {
                hash = sha256.ComputeHash(data);
            }
            return hash;
        }

        /// <summary>
        /// Returns the generated wallet address, using the RIPEMD160 hash of the public key.
        /// </summary>
        /// <param name="publicKey"></param>
        /// <returns></returns>
        public static byte[] GenerateWalletAddress(ECDiffieHellmanCngPublicKey publicKey)
        {
            byte[] encodedPk = SHash(publicKey.ToByteArray());
            RIPEMD160 ripemd160 = new RIPEMD160();
            return ripemd160.ComputeHash(encodedPk);
        }

        /// <summary>
        /// Returns the string hex of a byte[] representation of an object's hash.
        /// </summary>
        /// <param name="hash"></param>
        /// <returns></returns>
        public static string ToHumanReadableHash(byte[] hash)
        {
            return Convert.ToHexString(hash);
        }

        /// <summary>
        /// Returns the byte[] representation of a hgash's hex string.
        /// </summary>
        /// <param name="strHash"></param>
        /// <returns></returns>
        public static byte[] ToHashByteArray(string strHash)
        {
            return Convert.FromHexString(strHash);
        }

        /// <summary>
        /// Checks if two byte[] hashes are equal.
        /// </summary>
        /// <param name="val0"></param>
        /// <param name="val1"></param>
        /// <returns></returns>
        public static bool IsEqualHashes(byte[] val0, byte[] val1)
        {
            try
            {
                for(var i = 0; i < val1.Length; i++)
                {
                    if (val0[i] != val1[i])
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

        /// <summary>
        /// Checks if two byte[] hashes are equal, but also provides if one is greater than the other.
        /// </summary>
        /// <param name="val0"></param>
        /// <param name="val1"></param>
        /// <returns></returns>
        public static int CompareHashes(byte[] val0, byte[] val1)
        {
            if (val0 == val1) return 0;
            if (val0 == null) return -1;
            if (val1 == null) return 1;

            int last = Math.Min(val0.Length, val1.Length);
            for(var i = 0; i < last; i++)
            {
                byte val0b = val0[i];
                byte val1b = val1[i];
                if(val0b != val1b)
                {
                    return (val0b < val1b) ? -1 : 1;
                }
            }
            return 0;
        }

        /// <summary>
        /// Returns the merkleRoot of a list of txns.
        /// </summary>
        /// <param name="txns"></param>
        /// <returns></returns>
        public static byte[] GenerateMerkleTree(Transaction[] txns)
        {
            if (txns.Length % 2 != 0) txns = (Transaction[])txns.Concat(new Transaction[] { txns[txns.Length - 1] });
            byte[] temp = new byte[0];
            if(txns.Length == 2)
            {
                temp = temp.Concat(txns.First().Bytes()).ToArray();
                temp = temp.Concat(txns.Last().Bytes()).ToArray();
                return DHash(temp);
            }
            if(txns.Length == 1)
            {
                temp = temp.Concat(txns.First().Bytes()).ToArray();
                temp = temp.Concat(txns.First().Bytes()).ToArray();
                return DHash(temp);
            }
            return DHash((byte[])GenerateMerkleTree(new ArraySegment<Transaction>(txns, 0, (txns.Length/2-1)).ToArray()).Concat(GenerateMerkleTree(new ArraySegment<Transaction>(txns, (txns.Length/2), txns.Length/2-1).ToArray())));
        }
    }
}
