package org.nathanielbunch.ssblockchain;

import org.nathanielbunch.ssblockchain.core.SSBlock;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String[] args) throws Exception {

        System.out.println("Hello World, welcome to the SSBlockchain.");

        SSBlock genesis = SSBlock.BBuilder.newSSBlockBuilder()
                .setPreviousBlock(null)
                .setData("In the beginning, there was light...")
                .build();

        List<SSBlock> blockchain = new ArrayList<>();
        blockchain.add(genesis);
        SSBlock newBlock;
        SSBlock prevBlock = genesis;

        //make 20 blocks in the chain
        for(int i = 0; i < 20; i++){
            newBlock = nextBlock(prevBlock);
            blockchain.add(newBlock);
            prevBlock = newBlock;
            System.out.println(String.format("Block #%d has been added to the SS chain", newBlock.getIndex()));
            System.out.println(String.format("Hash: %s", newBlock.toString()));
        }

    }

    public static SSBlock nextBlock(SSBlock lastBlock) throws Exception {
        return SSBlock.BBuilder.newSSBlockBuilder()
                .setPreviousBlock(lastBlock.getBlockHash())
                .setData(String.format("I am block #%d", lastBlock.getIndex()+1))
                .build();
    }

    public static String printSSBlock(SSBlock block) {
        return String.format("[%s :: %s :: ID#%d :: %s ]", block.toString(), block.getTimestamp().toString(), block.getIndex(), block.getData().toString());
    }

}
