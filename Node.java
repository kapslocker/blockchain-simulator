import java.util.*;

public class Node{
  int id;
  // ArrayList<Transaction> transactions;
  // ArrayList<Block> blocks;
  ArrayList<Double> receivedStamps;
  Random rn;
  ArrayList<Node> peers;     //pointers to Nodes are stored here
  boolean fast;
  float coins;

  Node(int id, int n, ArrayList<Node> all){
    this.id = id;
    receivedStamps = new ArrayList<Double>();
    Collections.shuffle(all);
    rn = new Random();
    int numPeers = rn.nextInt(n-1) + 1;
    peers = new ArrayList<Node>();
    for(int i=0; i<numPeers; i++)
      peers.add(all.get(i));
  }

  //When to update balance??

  /*Transaction generateTransaction(double lambda){
    double t = Math.log(1-Math.random())/(-lambda);
    //Thread.sleep(t);
    int index = rn.nextInt(peers.size());
    double amt = Math.random()*coins;    //In theory, can spend entire money
    Transaction tr = new Transaction(id,peers.get(index),amt);
    return tr;
  }*/

  void sendTransaction(Transaction tr){
    for(int i=0; i<peers.size(); i++){
      //getlatency
      //insert in arraylist
    }
  }
}
