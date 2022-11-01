using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using yggdrasil_core.core.api.services;
using yggdrasil_core.core.ledger.chain;

namespace yggdrasil_core.core.api.controller
{
    [Route("api")]
    [ApiController]
    public class BlockchainController : ControllerBase
    {

        private BlockchainService _blockchainService;

        public BlockchainController()
        {
            this._blockchainService = new BlockchainService();
        }


        /// <summary>
        /// Returns the entire blockchain that is currently in memory.
        /// </summary>
        /// <exception cref="Exception"></exception>
        /// <returns></returns>
        [HttpGet("/blocks")]
        public HttpResponseMessage GetBlockchain(int? blocks)
        {
            Blockchain blockchain = new Blockchain();
            HttpResponseMessage response = new HttpResponseMessage();
            return response;
        }

        /// <summary>
        /// Retuns the data of a specific block.
        /// </summary>
        /// <exception cref="Exception"></exception>
        /// <returns></returns>
        [HttpGet("/block")]
        public HttpResponseMessage GetBlock(String blockhash)
        {
            //Block block = new Block();
            HttpResponseMessage response = new HttpResponseMessage();
            return response;
        }

        /// <summary>
        /// Starts up the miner (will be deprecated eventually).
        /// </summary>
        /// <exception cref="Exception"></exception>
        /// <returns></returns>
        [HttpGet("/mine")]
        public HttpResponseMessage MineBlock()
        {
            HttpResponseMessage response = new HttpResponseMessage();
            return response;
        }

        /// <summary>
        /// Returns the current wallet or all wallets that are present in the system.
        /// </summary>
        /// <exception cref="Exception"></exception>
        /// <returns></returns>
        [HttpGet("/wallet")]
        public HttpResponseMessage getWallet(bool? allWallets)
        {
            //return new ResponseEntity<>(this.service.getWallet(allWallets), HttpStatus.OK);
            HttpResponseMessage response = new HttpResponseMessage();
            return response;
        }

        /// <summary>
        /// Creates a new wallet, returning the private key.
        /// </summary>
        /// <exception cref="Exception"></exception>
        /// <returns></returns>
        [HttpPost("/createWallet")]
        public HttpResponseMessage createNewWallet(String walletName)
        {
            //return new ResponseEntity<>(this.service.createWallet(walletName), HttpStatus.OK);
            HttpResponseMessage response = new HttpResponseMessage();
            return response;
        }

        /// <summary>
        /// Switch currently selected wallets.
        /// </summary>
        /// <exception cref="Exception"></exception>
        /// <returns></returns>
        [HttpPut("/selectWallet")]
        public HttpResponseMessage switchNewWallet(String address)
        {
            //return new ResponseEntity<>(this.service.selectWallet(address), HttpStatus.OK);
            HttpResponseMessage response = new HttpResponseMessage();
            return response;
        }

        /// <summary>
        /// Create a new txn.
        /// </summary>
        /// <exception cref="Exception"></exception>
        /// <returns></returns>
        [HttpPut("/transaction")]
        public HttpResponseMessage putTransaction([FromBody]Object data) 
        {
            /*
            logger.trace("Received new data: {}", data);
            BasicTransaction transaction = objectMapper.treeToValue(data, BasicTransaction.class);
            this.service.addNewTransaction(transaction);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
            */
            HttpResponseMessage response = new HttpResponseMessage();
            return response;
        }

        /// <summary>
        /// Returns a number of txns.
        /// </summary>
        /// <exception cref="Exception"></exception>
        /// <returns></returns>
        [HttpGet("/transaction")]
        public HttpResponseMessage getTransaction(int? transactions)
        {
            /*
                if(transactions == null || transactions <= 0) {
                    transactions = 1;
                }
                return new ResponseEntity<>(this.service.getTransaction(transactions), HttpStatus.OK);
            */
            HttpResponseMessage response = new HttpResponseMessage();
            return response;
        }

        /// <summary>
        /// Tests a feature within the APIs.
        /// </summary>
        /// <exception cref="Exception"></exception>
        // Used to test in-development features
        [HttpHead("/testFeature")]
        public void testFeature()
        {
            //this.service.testSigning();
        }
    }
}
