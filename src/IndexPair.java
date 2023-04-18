import java.util.ArrayList;
import java.util.HashMap;

public class IndexPair {
    HashMap<Integer, ArrayList<Pair>> hashIndex;
    ArrayList<Pair>[] arrayIndex;

    public IndexPair(HashMap<Integer, ArrayList<Pair>> hashIndex, ArrayList<Pair>[] arrayIndex) {
        this.hashIndex = hashIndex;
        this.arrayIndex = arrayIndex;
    }
}
