public class Transaction{
  int tID;
  int fromID;
  int toID;
  double amount;

  //how to generate transaction ID?
  Transaction(int tID, int fromID, int toID, double amount){
    this.tID = tID;
    this.fromID = fromID;
    this.toID = toID;
    this.amount = amount;
  }

  Transaction(Transaction tr){
    tID = tr.tID;
    fromID = tr.fromID;
    toID = tr.toID;
    amount = tr.amount;
  }
}
