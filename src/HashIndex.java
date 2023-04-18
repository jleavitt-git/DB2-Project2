import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class HashIndex {

    //Integer RandomV, Arraylist of ID's
    HashMap<Integer, ArrayList<Pair>> index;

    public ArrayList<Pair> getValuesAtRandomV(int randomV){
        return index.get(randomV);
    }

    public HashIndex(HashMap<Integer, ArrayList<Pair>> index){
        this.index = index;
    }
}
