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
  final int n = 42;
  Random rn;
  double currTime;
  float z;
  int prop_ij;
  int currID;
  int uniqueBlockID;
  PriorityQueue<Event> queue;
  ArrayList<ArrayList<Transaction>> transactions;
  ArrayList<ArrayList<Block>> blocks;
  ArrayList<Node> nodes;

  Simulator(){
    Comparator<Event> comparator = new TimeComparator();
    PriorityQueue<Event> queue = new PriorityQueue<Event>(comparator);
    Random rn = new Random();
    prop_ij = rn.nextInt(491) + 10;
    currTime = 0.0;
    uniqueBlockID = 0;
    currID = 1;
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
    return(prop_ij+b+c);
  }

  void doAllEvents(double endTime){

    while(currTime <= endTime){
        Event e = queue.remove();
        double t = e.scTime;
        currTime = t;
      //do things here(call Event.execute(this))
    }
  }

  int genBlockID(){
      return uniqueBlockID++ ;
  }
  void initTransaction(){

  }
}
