import java.util.Random;
public class Event{
    int type;
    double scTime;
    double crTime;

    Node genNode;
    Node node;
    Block block;
    Transaction transaction;
    Random randomno;


    public Event(int type, double scTime, Node node, Block block, Transaction transaction, Node genNode){
        this.type = type;
        this.scTime = scTime;
        this.node = node;
        this.block = block;
        this.node = node;
        this.transaction = transaction;
        this.genNode = genNode;
        this.randomno = new Random();
    }

    void execute(Simulator s){
        switch(type){
            case 0:
                // Block generate
                System.out.println("Block Generate Event:");
                System.out.println("Scheduled Time: " + Double.toString(scTime));
                System.out.println("Node: " + Integer.toString(node.id));
                generateBlock(s,crTime,scTime);
                System.out.print("\n");
                break;
            case 1:
                // Transaction generate
                System.out.println("Transaction Generate Event:");
                System.out.println("Scheduled Time: " + Double.toString(scTime));
                System.out.println("Node: " + Integer.toString(node.id));
                generateTransaction(s);
                System.out.print("\n");
                break;
            case 2:
                // Block receive
                System.out.println("Block Receive Event:");
                System.out.println("Scheduled Time: " + Double.toString(scTime));
                System.out.println("Node: " + Integer.toString(node.id));
                System.out.println("Block ID: " + Integer.toString(block.bID));
                receiveBlock(s,scTime);
                System.out.print("\n");
                break;
            case 3:
                // Transaction receive
                System.out.println("Transaction Receive Event:");
                System.out.println("Scheduled Time: " + Double.toString(scTime));
                System.out.println("Node: " + Integer.toString(node.id));
                System.out.println("Transaction ID: " + Integer.toString(transaction.tID));
                receiveTransaction(s, scTime);
                System.out.print("\n");
                break;

        }
    }

    void generateTransaction(Simulator s){
      int toID = node.id;
      while(toID == node.id){
        toID = randomno.nextInt(s.n);
      }
      float currCoins = s.nodes.get(node.id).coins;
      float fraction = randomno.nextFloat();
      System.out.println("NextFLoat: " + Float.toString(fraction));
      float transactionAmt = currCoins*fraction;
      Transaction newTransaction = new Transaction(s.currID, node.id, toID, transactionAmt);
      System.out.println("New Transaction ID: " + Integer.toString(newTransaction.tID) + " From:" + Integer.toString(newTransaction.fromID) + " To: " + Integer.toString(newTransaction.toID) + " Amount: " + Double.toString(newTransaction.amount));
      s.currID++;
      System.out.println("Before Coins: " + Float.toString(s.nodes.get(node.id).coins));
    //   System.out.println("After Coins: " + Float.toString(s.nodes.get(node.id).coins));
      s.nodes.get(node.id).coins -= transactionAmt;
      System.out.println("After Coins: " + Float.toString(s.nodes.get(node.id).coins));
      s.nodes.get(toID).coins += transactionAmt;

      //add transaction to current node's list
      s.transactions.get(node.id).add(newTransaction);

      //create next transaction event for this node
      double lambda = 10;   //arbit value
      double t = Math.log(1-Math.random())/(-lambda);
      Event nextTransactionEvent = new Event(1, scTime + t, node, null, null, node);
      nextTransactionEvent.crTime = scTime;
      s.queue.add(nextTransactionEvent);

      //create next receive event for its neighbours
      if(s.nodes.get(node.id).peers == null)
        System.out.println("Peers is NUll" + String.valueOf(node.id));

      int size = s.nodes.get(node.id).peers.size();
      for(int i=0; i<size; i++){
        Event receiveTransactionEvent;
        double latency = s.simulateLatency(node.id, s.nodes.get(node.id).peers.get(i).id, 10);
        receiveTransactionEvent = new Event(3, scTime+latency, s.nodes.get(node.id).peers.get(i), null, newTransaction, node);  //take receive event constructor
        receiveTransactionEvent.crTime = scTime;
        s.queue.add(receiveTransactionEvent);
      }
    }

    void receiveTransaction(Simulator s, double scheduledTime){
        boolean found = false;
        for(int i=0; i<s.transactions.get(node.id).size(); i++){
            Transaction t = s.transactions.get(node.id).get(i);
            if(t.tID == transaction.tID){
                found = true;
                break;
            }
        }
        if(!found){
            Transaction tr = new Transaction(transaction);
            s.transactions.get(node.id).add(tr);
            for(int i=0; i<node.peers.size(); i++){
                if(node.peers.get(i).id != genNode.id){
                    double latency = s.simulateLatency(node.id, node.peers.get(i).id, 1);
                    Event e = new Event(3, scheduledTime+latency, node.peers.get(i), null, tr, node);
                    e.crTime = scTime;
                    s.queue.add(e);
                }
            }
        }
    }

    int lambda = 10;                        // TODO: Set this as a simulation parameter
    void receiveBlock(Simulator s, double scheduledTime){
        boolean found = false;
        for(int i=0; i<s.blocks.get(node.id).size(); i++){
            Block b = s.blocks.get(node.id).get(i);
            if(b.bID == block.bID){
                found = true;
                break;
            }
        }

        if(!found){
            double v = (new Random()).nextDouble();
            double T_k = Math.log(1 - v)/(-lambda);

            int len = 0;
            Block blk = new Block();
            for(int i=0; i<s.blocks.get(node.id).size(); i++){
                if(s.blocks.get(node.id).get(i).bID == block.previousBlock.bID){
                    len = s.blocks.get(node.id).get(i).length;
                    blk = s.blocks.get(node.id).get(i);
                    break;
                }
            }

            Block blockReceived = new Block(block);      //Added Copy constructor(POSSIBLE BUG)
            blockReceived.length = len+1;
            blockReceived.previousBlock = blk;
            s.blocks.get(node.id).add(blockReceived);
            s.nodes.get(node.id).receivedStamps.add(scheduledTime);
            /*for(int i=0; i<node.peers.size(); i++){       //Incorrect perhaps(How can a node know if its neighbour has a particular block)
                boolean found = false;
                for(int j=0; j<s.blocks.get(node.peers.get(i).id).size(); j++){
                    if(block.bID == s.blocks.get(node.peers.get(i).id).bID){
                        found = true;
                        break;
                    }
                }
                if(!found){
                    double latency = s.simulateLatency(node.id, node.peers.get(i).id, 1);
                    Event e = new Event(2, scheduledTime+latency, node.peers.get(i), blk, null);
                    e.crTime = scTime;
                    s.queue.add(e);
                }
            }*/

            for(int i=0; i<node.peers.size(); i++){
                if(node.peers.get(i).id != genNode.id){
                    double latency = s.simulateLatency(node.id, node.peers.get(i).id, 1);
                    Event e = new Event(2, scheduledTime+latency, node.peers.get(i), blockReceived, null, node);
                    e.crTime = scTime;
                    s.queue.add(e);
                }
            }

            Event e = new Event(0, scheduledTime+T_k, node, null, null, node);
            e.crTime = scTime;
            s.queue.add(e);
        }
    }

    void generateBlock(Simulator s, double creationTime, double scheduledTime){
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
            Block id = new Block();
            for(int i=0;i<s.blocks.get(node.id).size();i++){
                Block b = s.blocks.get(node.id).get(i);
                if(b.length > len){
                    len = b.length;
                    id = b;
                }
            }

            blk = new Block(s.genBlockID(),scheduledTime, id, node.id, len + 1);
            System.out.println("New Block ID: " + Integer.toString(blk.bID) + " Creator:" + Integer.toString(blk.creatorID) + " Length: " + Integer.toString(blk.length));
            for(int i = 0;i < s.transactions.get(node.id).size();i++){
                Transaction t = s.transactions.get(node.id).get(i);

                int len1 = 0;
                Block lastBlock = new Block();
                for(int j=0; j<s.blocks.get(node.id).size(); j++){
                    if(s.blocks.get(node.id).get(j).length > len1){
                        len1 = s.blocks.get(node.id).get(j).length;
                        lastBlock = s.blocks.get(node.id).get(j);
                    }
                }


                if(lastBlock.transactions.contains(t)){
                    s.transactions.get(node.id).remove(i);
                }
                else{
                    blk.transactions.add(t);
                }
            }
            s.blocks.get(node.id).add(blk);
            broadcastBlock(s, blk, scheduledTime);
        }
    }

    void broadcastBlock(Simulator s, Block blk, double scheduledTime){
        for(int i=0; i<node.peers.size(); i++){
            double latency = s.simulateLatency(node.id, node.peers.get(i).id, 1);
            Event e = new Event(2, scheduledTime+latency, node.peers.get(i), blk, null, node);
            e.crTime = scTime;
            s.queue.add(e);
        }
    }
}
