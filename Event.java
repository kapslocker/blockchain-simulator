import java.util.Random;
public class Event{
    int type;
    double scTime;
    double crTime;

    Node node;
    Block block;
    Transaction transaction;

        Transaction newTransaction;
        Random randomno;
        int nodeID;


    public Event(int type, double scTime, Node node, Block block, Transaction transaction){
        this.type = type;
        this.scTime = scTime;
        this.node = node;
        this.block = block;
        this.nodeID = node.id;
        this.transaction = transaction;
    }


    Event(int type, double scTime, int nodeID, Transaction newTransaction)
    {
      randomno = new Random();
      this.type = type;
      this.scTime = scTime;
      this.nodeID = nodeID;
      this.newTransaction = newTransaction;
    }


    void execute(Simulator s){
        switch(type){
            case 0:
                // Block generate
                generateBlock(crTime,scTime);
                break;
            case 1:
                // Transaction generate
                generateTransaction(Simulator s);

                break;
            case 2:
                // Block receive
                receiveBlock(scTime);
                break;
            case 3:
                // Transaction receive
                //receiveTrans();
                break;

        }
    }

    void generateTransaction(Simulator s)
    {
      int toID = nodeID;
      while(toID == nodeID)
      {
        toID = randomno.nextInt(s.n);
      }
      float currCoins = s.nodes[toID].coins;
      float fraction = randomno.nextFloat();
      float transactionAmt = currCoins*fraction;
      Transaction newTransaction = new Transaction(s.currID, nodeID, toID, transactionAmt);
      s.currID++;
      s.nodes.get(nodeID).coins -= transactionAmt;
      s.nodes.get(toID).coins += transactionAmt;

      //add transaction to current node's list
      s.transactions.get(nodeID).add(newTransaction);

      //create next transaction event for this node
      double lambda = 10;   //arbit value
      double t = Math.log(1-Math.random())/(-lambda);
      Event nextTransactionEvent = new Event(1, scTime + t, nodeID);
      s.queue.add(nextTransactionEvent);

      //create next receive event for its neighbours
      int size = s.nodes.get(nodeID).peers.size();
      for(int i=0; i<size; i++)
      {
        Event receiveTransactionEvent;
        double latency = s.simulateLatency(nodeID, s.nodes.get(nodeID).peers.get(i), 10);
        //take receive event constructor
        s.queue.add(receiveTransactionEvent);

      }
    }

    int lambda = 10;                        // TODO: Set this as a simulation parameter
    void receiveBlock(double scheduledTime){
        double v = (new Random()).nextDouble();
        double T_k = Math.log(1 - v)/(-lambda);

        int len = 0;
        Block blk;
        for(int i=0; i<Simulator.blocks.get(node.id).size(); i++){
            if(Simulator.blocks.get(node.id).get(i).bID == block.prevBlock.bID){
                len = Simulator.blocks.get(node.id).get(i).length;
                blk = Simulator.blocks.get(node.id).get(i);
                break;
            }
        }
        block.length = len+1;
        block.prevBlock = blk;
        Simulator.blocks.get(node.id).add(block);

        for(int i=0; i<node.peers.size(); i++){
            boolean found = false;
            for(int j=0; j<Simulator.blocks.get(node.peers.get(i).id).size(); j++){
                if(block.bID == Simulator.blocks.get(node.peers.get(i).id).bID){
                    found = true;
                    break;
                }
            }
            if(!found){
                double latency = Simulator.simulateLatency(node.id, node.peers.get(i).id, 1);
                Event e = new Event(2, scheduledTime+latency, node.peers.get(i), blk, null);
                e.crTime = scTime;
                Simulator.queue.add(e);
            }
        }

        Event e = new Event(0,scheduledTime+T_k,node,null,null);
        e.creationTime = scTime;
        Simulator.queue.add(e);
    }

    void generateBlock(int creationTime, double scheduledTime){
        Block blk;
        boolean found = false;
        for(int i = 0; i < node.receivedStamps.size(); i++ ){
            if(node.receivedStamps.get(i) < scheduledTime && node.receivedStamps.get(i) > creationTime){
                found  = true;
                break;
            }
        }
        if(!found){
            int len = 0;
            Block id;
            for(int i=0;i<Simulator.blocks.get(node.id).size();i++){
                Block b = Simulator.blocks.get(node.id).get(i);
                if(b.length > len){
                    len = b.length;
                    id = b;
                }
            }

            blk = new Block(Simulator.genBlockID(),scheduledTime, id, node.id, len + 1);

            for(int i = 0;i < Simulator.transactions.get(node.id).size();i++){
                Transaction t = Simulator.transactions.get(node.id).get(i);
                if(block.transactions.contains(t)){
                    Simulator.transactions.get(nodeID).remove(i);
                }
                else{
                    blk.transactions.add(t);
                }
            }
            Simulator.blocks.get(node.id).add(blk);
            broadcastBlock(blk, scheduledTime);
        }
    }

    void broadcastBlock(Block blk, double scheduledTime){
        for(int i=0; i<node.peers.size(); i++){
            double latency = Simulator.simulateLatency(node.id, node.peers.get(i).id, 1);
            Event e = new Event(2, scheduledTime+latency, node.peers.get(i), blk, null);
            e.crTime = scTime;
            Simulator.queue.add(e);
        }
    }
}
