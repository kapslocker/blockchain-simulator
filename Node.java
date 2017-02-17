import java.util.*;

public class Node{
  int id;
  // ArrayList<Transaction> transactions;
  // ArrayList<Block> blocks;
  ArrayList<double> receivedStamps;
  Random rn;
  ArrayList<Integer> peers;
  boolean fast;
  float coins;

  Node(int id, int n){
    this.id = id;
    receivedStamps = new ArrayList<double>();
    ArrayList<Integer> all = new ArrayList<>();
    for(int i=0; i<n; i++){
      if(id != i)
        all.add(i);
    }
    Collections.shuffle(all);
    rn = new Random();
    int numPeers = rn.nextInt(n-1) + 1;
    peers = new ArrayList<>();
    for(int i=0; i<numPeers; i++)
      peers.add(all.get(i));
  }

  //When to update balance??

  Transaction generateTransaction(double lambda){
    double t = Math.log(1-Math.random())/(-lambda);
    Thread.sleep(t);  //t is in millis
    int index = rn.nextInt(peers.size());
    float amt = Math.random()*coins;    //In theory, can spend entire money
    Transaction tr = new Transaction(id,peers.get(index),amt);
    return tr;
  }

  void sendTransaction(Transaction tr){
    for(int i=0; i<peers.size(); i++){
      //getlatency
      //insert in arraylist
    }
  }
}
