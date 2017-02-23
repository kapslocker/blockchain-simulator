import java.util.*;
import java.io.*;

class TimeComparator implements Comparator<Event>{
  @Override
  public int compare(Event e1, Event e2){
    if(e1.scTime < e2.scTime)
      return -1;
    if(e1.scTime > e2.scTime)
      return 1;
    return 0;
  }
}

public class Simulator{
  static final int n = 20;         //take input
  Random rn;
  double currTime;
  final double z = 0.5;      //take input
  int currID;
  int uniqueBlockID;
  Comparator<Event> comparator;
  PriorityQueue<Event> queue;
  ArrayList<ArrayList<Transaction>> transactions;
  ArrayList<ArrayList<Block>> blocks;
  ArrayList<Node> nodes;
  double[][] prop_ij = new double[n][n];

  Simulator(){
    comparator = new TimeComparator();
    queue = new PriorityQueue<Event>(comparator);
    nodes = new ArrayList<Node>();
    blocks = new ArrayList<ArrayList<Block>>(n);
    transactions = new ArrayList<ArrayList<Transaction>>(n);
    for(int i=0; i<n; i++){
        blocks.add(i, new ArrayList<Block>());
        transactions.add(i, new ArrayList<Transaction>());
    }

    rn = new Random();
    //prop_ij = rn.nextInt(491) + 10;
    currTime = 0.0;
    uniqueBlockID = 1;      //0 for Gen block
    currID = 1;
  }

  void init(){
      //create nodes
      Node newNode;
      double nextRand;
      for(int i=0; i<n; i++){
          nextRand = Math.random();
          float amount = 100*rn.nextFloat();
          //double mean = Math.random()*4;
          double mean = 1.0;
          if(nextRand >= z){ //fast node
              newNode = new Node(i, true, mean, amount);
              nodes.add(newNode);
          }
          else{                  //slow node
              newNode = new Node(i, false, mean, amount);
              nodes.add(newNode);
          }
      }


      setPeers();
    //   for(int i=0; i<n; i++){
    //       ArrayList<Node> all = new ArrayList<Node>(nodes);
    //       nodes.get(i).setPeers(n, all);
    //   }

      for(int i=0; i<n; i++){
          for(int j=0; j<n; j++){
            prop_ij[i][j] = rn.nextInt(491) + 10;
            prop_ij[i][j] /= 1000.0;
          }
      }

      //add genesys
      Block genBlock;
      for(int i=0; i<n; i++){
          genBlock = new Block(0, 0.0, null, -1, 1);
          blocks.get(i).add(genBlock);
          //System.out.println(blocks.get(i).get(0).length);
      }

      double lambda = 10;   //arbit value
      double t;
      Event newBlockGenerate;
      for(int i=0; i<n; i++){
          t = Math.log(1-Math.random())/(-lambda);
          newBlockGenerate = new Event(0, t, nodes.get(i), null, null, null);
          queue.add(newBlockGenerate);
      }

      //add first transaction of every node
      Event newTransactionEvent;
      for(int i=0; i<n; i++){
          t = Math.log(1-Math.random())/(-lambda);
          newTransactionEvent = new Event(1, t, nodes.get(i), null, null, null);
          queue.add(newTransactionEvent);
      }
      for(int i=0; i<n; i++)
      {
          //System.out.print(Integer.toString(i) + " Peers: ");
          int size = nodes.get(i).peers.size();
          for(int j=0; j<size; j++)
          {
    //          System.out.print(Integer.toString(nodes.get(i).peers.get(j).id) + " ");
          }
//          System.out.print("\n");
      }
  }

  void setPeers(){
      int m = rn.nextInt((n-1)*(n-2)/2) + (n-1);
      Set<String> peerPairs= new HashSet<String>();
      while(m>0){
          int p1 = rn.nextInt(n);
          int p2 = p1;
          while(p1 == p2){
              p2 = rn.nextInt(n);
          }
          String s;
          if(p1<p2)
              s = Integer.toString(p1) + " " + Integer.toString(p2);
          else
              s = Integer.toString(p2) + " " + Integer.toString(p1);

          if(peerPairs.add(s))
              m--;
      }

      Iterator<String> itr = peerPairs.iterator();
      while(itr.hasNext()){
          String s = itr.next();
          String[] splitPairs = s.split(" ");
          int p1 = Integer.parseInt(splitPairs[0]);
          int p2 = Integer.parseInt(splitPairs[1]);
          nodes.get(p1).peers.add(nodes.get(p2));
          nodes.get(p2).peers.add(nodes.get(p1));
      }
  }

  double simulateLatency(int i, int j, int size){  //void??
    Node pi = nodes.get(i);
    Node pj = nodes.get(j);
    double c_ij;
    if(pi.fast && pj.fast)
      c_ij = 100000000.0;                         //bps
    else
      c_ij = 5000000.0;                           // bps
    double b = (((float)size*8.0*100000.0)/c_ij);   //in seconds
    double lambda = c_ij/(12*8000.0);               //bps
//    System.out.println("Checking :" + Math.random());
    double c = Math.log(1-Math.random())/(-lambda);  //unit? millis?
//    System.out.println("C : "+Double.toString(c)+ " "+ Double.toString(lambda));
//    System.out.println("Latency: " + Double.toString((prop_ij[i][j]+b+c)));
    return prop_ij[i][j]+b+c;
  }

  void doAllEvents(double endTime){

    while(currTime <= endTime){
        Event e = queue.remove();
        double t = e.scTime;
        currTime = t;
        e.execute(this);
    }
  }

  void printNode(int pos, PrintWriter w){
      for(int i=0; i < blocks.get(pos).size(); i++){
          if(blocks.get(pos).get(i).previousBlock != null){
            w.println(String.valueOf(blocks.get(pos).get(i).previousBlock.bID) + "->" + String.valueOf(blocks.get(pos).get(i).bID));
          }
      }

      w.println();

      for(int i=0; i<blocks.get(pos).size(); i++){
          w.println(String.valueOf(blocks.get(pos).get(i).bID) + ": " + String.valueOf(blocks.get(pos).get(i).timestamp));
          //System.out.println(String.valueOf(blocks.get(pos).get(i).bID) + ": " + String.valueOf(blocks.get(pos).get(i).timestamp));
      }
  }

  int genBlockID(){
      return uniqueBlockID++;
  }

  public static void main(String[] args) {
      Simulator sim = new Simulator();
      sim.init();
      //System.out.println(sim.nodes.get(0).peers == null);
      sim.doAllEvents(4.0);

      /*for(int i=0;i<sim.nodes.size();i++){
          if(sim.nodes.get(i).type){
              sim.printNode(i, null);
              break;
          }
      }*/

      for(int i=0;i<sim.blocks.size();i++){
          PrintWriter writer;
          try{
            //FORMAT nodenumber_fast_cputype
            writer = new PrintWriter(Integer.toString(i)+ "_" + Boolean.toString(sim.nodes.get(i).fast) + "_" + Boolean.toString(sim.nodes.get(i).type) +".txt", "UTF-8");
            sim.printNode(i,writer);
            writer.close();
          }
          catch(Exception e){
          }
      }
  }
}
