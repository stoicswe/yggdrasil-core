namespace yggdrasil_core.core.api.services
{
    public class BlockchainService
    {
        private static BlockchainService? _instance;
        public static BlockchainService GetBlockchainServiceInstance()
        {
            if (_instance == null) _instance = new BlockchainService();
            return _instance;
        }

        public BlockchainService()
        {

        }
    }
}
