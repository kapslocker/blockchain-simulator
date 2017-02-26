import java.util.*;

public class Block{
    int bID;
    double timestamp;
    Block previousBlock;
    int creatorID;
    int length;
    HashSet<Transaction> transactions;

    Block(){

    }

    Block(int bID, double ts, Block prevBlock, int creatorID, int length){
        this.bID = bID;
        timestamp = ts;
        previousBlock = prevBlock;
        this.creatorID = creatorID;
        this.length = length;
        this.transactions = new HashSet<Transaction>();
    }

    Block(Block blk){  //set length and previousBlock when using this constructor
        this.bID = blk.bID;
        this.timestamp = blk.timestamp;
        this.creatorID = blk.creatorID;
        this.transactions = new HashSet<Transaction>(blk.transactions);
    }
}
