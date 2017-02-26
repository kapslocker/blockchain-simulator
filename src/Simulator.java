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
  static int n;         //take input
  Random rn;
  double currTime;
  static double z;      //take input
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

      for(int i=0; i<n; i++){
          for(int j=0; j<n; j++){
            prop_ij[i][j] = rn.nextInt(491) + 10;
            prop_ij[i][j] /= 1000.0;
          }
      }

      //add genesis block
      Block genBlock;
      for(int i=0; i<n; i++){
          genBlock = new Block(0, 0.0, null, -1, 1);
          blocks.get(i).add(genBlock);
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
  }
/*
* Creates node pairs and adds them to the Graph.
*/
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
/*
* Return Latency between two nodes i,j
*/
  double simulateLatency(int i, int j, int size){
    Node pi = nodes.get(i);
    Node pj = nodes.get(j);
    double c_ij;
    if(pi.fast && pj.fast)
      c_ij = 100000000.0;                         //bps
    else
      c_ij = 5000000.0;                           // bps
    double b = (((float)size*8.0*100000.0)/c_ij);   //in seconds
    double lambda = c_ij/(12*8000.0);               //bps
    double c = Math.log(1-Math.random())/(-lambda); // Exponential Distribution
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
  /*
  * Prints data to be used for visualisation for a given node.
  */
  void printNode(int pos, PrintWriter w){
      for(int i=0; i < blocks.get(pos).size(); i++){
          if(blocks.get(pos).get(i).previousBlock != null){
            w.println(String.valueOf(blocks.get(pos).get(i).previousBlock.bID) + "->" + String.valueOf(blocks.get(pos).get(i).bID));
          }
      }

      w.println();

      for(int i=0; i<blocks.get(pos).size(); i++){
          w.println(String.valueOf(blocks.get(pos).get(i).bID) + ": " + String.valueOf(blocks.get(pos).get(i).timestamp));
      }
  }

  int genBlockID(){
      return uniqueBlockID++;
  }

  static void fetchInput(String[] inp)throws IllegalStateException{
      if(inp==null || inp.length==0){
          System.out.println("Please enter the values of N and Z. See Usage for format.");
          throw new IllegalStateException();
      }
      if(inp[0].equals("-h") || inp[0].equals("--help")){
          System.out.println("Usage: ");
          System.out.println("java Simulator n z");
          System.out.println("n\tNo. of nodes in the simulation.");
          System.out.println("z\tpercentage of SLOW nodes in the network. (0-1)");
          throw new IllegalStateException();
      }
      if(inp.length<2){
          System.out.println("Missing values. See Usage for format");
          throw new IllegalStateException();
      }
      try{
          n = Integer.parseInt(inp[0]);
          z = Double.parseDouble(inp[1]);
          //System.out.println(inp[0] + inp[1]);

      }catch(Exception e){
          System.out.println("n(z) need to be of type Integer(Double)");
          throw new IllegalStateException();
      }
  }
    public static void main(String[] args) {
        try{
            fetchInput(args);
            Simulator sim = new Simulator();
            sim.init();
            sim.doAllEvents(4.0);                       // Simulation runs for a simulated duration of 4 seconds.
            for(int i=0;i<sim.blocks.size();i++){
                PrintWriter writer;
                try{
                    //FORMAT: nodenumber_fast_cputype (fast = true for fast node and vice-versa)
                    writer = new PrintWriter("../outputs/"+Integer.toString(i)+ "_" +
                            Boolean.toString(sim.nodes.get(i).fast) + "_" +
                            Boolean.toString(sim.nodes.get(i).type) +".txt", "UTF-8");
                    sim.printNode(i,writer);
                    writer.close();
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }
        catch(Exception e){
        }

    }
}
