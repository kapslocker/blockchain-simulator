public class Transaction{
  int tID;
  int fromID;
  int toID;
  float amount;

  //how to generate transaction ID?
  Transaction(int fromID, int toID, float amount){
    this.fromID = fromID;
    this.toID = toID;
    this.amount = amount;
  }
}
