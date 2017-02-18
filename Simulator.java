import java.util.*;

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
  static final int n = 4;         //take input
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
          if(nextRand >= z){ //fast node
              newNode = new Node(i, true, 10.0, amount);
              nodes.add(newNode);
          }
          else{                  //slow node
              newNode = new Node(i, false, 10.0, amount);
              nodes.add(newNode);
          }
      }


      setPeers();
    //   for(int i=0; i<n; i++){
    //       ArrayList<Node> all = new ArrayList<Node>(nodes);
    //       nodes.get(i).setPeers(n, all);
    //   }

      for(int i=0; i<n; i++){
          for(int j=0; j<n; j++)
            prop_ij[i][j] = rn.nextInt(491) + 10;
      }

      //add genesys
      Block genBlock;
      for(int i=0; i<n; i++){
          genBlock = new Block(0, 0.0, null, -1, 1);
          blocks.get(i).add(genBlock);
          System.out.println(blocks.get(i).get(0).length);
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
          System.out.print(Integer.toString(i) + " Peers: ");
          int size = nodes.get(i).peers.size();
          for(int j=0; j<size; j++)
          {
              System.out.print(Integer.toString(nodes.get(i).peers.get(j).id) + " ");
          }
          System.out.print("\n");
      }
  }

  void setPeers()
  {
      int m = rn.nextInt((n-1)*(n-2)/2) + (n-1);
      Set<String> peerPairs= new HashSet<String>();
      while(m>0)
      {
          int p1 = rn.nextInt(n);
          int p2 = p1;
          while(p1 == p2)
          {
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
      while(itr.hasNext())
      {
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
    int c_ij;
    if(pi.fast && pj.fast)
      c_ij = 100;
    else
      c_ij = 5;
    double b = (((float)size*8.0)/c_ij)*1000; //in microseconds
    double lambda = (12*8.0)/c_ij;    //Mbps -> kb per ms
    double c = Math.log(1-Math.random())/(-lambda);  //unit? millis?
    System.out.println("Latency: " + Double.toString((prop_ij[i][j]+b+c)/5000));
    return (prop_ij[i][j]+b+c)/5000;
  }

  void doAllEvents(double endTime){

    while(currTime <= endTime){
        Event e = queue.remove();
        double t = e.scTime;
        currTime = t;
        e.execute(this);
    }
  }

  int genBlockID(){
      return uniqueBlockID++;
  }

  public static void main(String[] args) {
      Simulator sim = new Simulator();
      sim.init();
      System.out.println(sim.nodes.get(0).peers == null);
      sim.doAllEvents(100.0);
  }
}
