import java.util.*;

public class Node{
    int id;
    ArrayList<Double> receivedStamps;
    Random rn;
    ArrayList<Node> peers;     //pointers to Nodes are stored here
    boolean fast;
    double lambda;
    float coins;
    boolean type;           // True => Fast CPU

    Node(int id, boolean fast, double lambda, float coins){
        this.id = id;
        this.fast = fast;
        this.lambda = lambda;
        this.coins = coins;
        if(lambda > 0.5 ){
            this.type = true;
        }
        else{
            this.type = false;
        }
        receivedStamps = new ArrayList<Double>();
        peers = new ArrayList<Node>();
    }

    void setPeers(int n, ArrayList<Node> all){
        Collections.shuffle(all);
        rn = new Random();
        int numPeers = rn.nextInt(n-1) + 1;

        for(int i=0; i<numPeers; i++){
            if(id != all.get(i).id)
                peers.add(all.get(i));
        }
    }
}
