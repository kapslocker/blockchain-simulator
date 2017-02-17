public class Simulator
{
  final int n;
  Random rn;
  float z;
  int prop_ij;
  ArrayList<ArrayList<Transaction>> transactions;
  ArrayList<ArrayList<Block>> blocks;
  ArrayList<Node> nodes;

  Simulator(){
    Random rn = new Random();
    prop_ij = rn.nextInt(491) + 10;
  }

  double simulateLatency(int i, int j, int size){  //void??
    Node pi = nodes.get(i);
    Node pj = nodes.get(j);
    int c_ij;
    if(pi.fast && pj.fast)
      c_ij = 100;
    else
      c_ij = 5;
    double b = (((float)size*8.0)/c_ij)*1000;
    double lambda = (12*8.0)/c_ij;    //Mbps -> kb per ms
    double c = Math.log(1-Math.random())/(-lambda);  //unit? millis?
    return(prop_ij+b+c);
  }

  void initTransaction(){

  }  
}
