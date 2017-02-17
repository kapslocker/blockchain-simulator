public class Block{
    int bID;
    double timestamp;
    Block previousBlock;
    int creatorID;
    int length;
    HashSet<Transaction> transactions;
    Block(int bID, double ts, Block prevBlock, int creatorID, int length ){
        this.bID = bID;
        timestamp = ts;
        previousBlock = prevBlock;
        this.creatorID = creatorID;
        this.length = length;
    }
}
