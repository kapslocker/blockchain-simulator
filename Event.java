public class Event{
    int type;
    double scTime;

    void execute(Simulator s){
        switch(type){
            case 0:
                // Block generate
                break;
            case 1:
                // Transaction generate
                break;
            case 2:
                // Block receive
                break;
            case 3:
                // Transaction receive
                break;

        }
    }
}
