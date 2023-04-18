import java.io.IOException;
import java.util.ArrayList;


public class ArrayIndex {

    ArrayList<Pair>[] index;

    public ArrayIndex(ArrayList<Pair>[] index){
        this.index = index;
    }

    //Array is 0 based indexing, so must minus the randomV by 1 to get proper index
    public ArrayList<Pair> getValuesAtRandomV(int randomV){
        return index[randomV-1];
    }

    public ArrayList<Pair> getValuesinRange(int v1, int v2) {
        ArrayList<Pair> records = new ArrayList<>();
        for(int i = v1+1; i < v2; i++){
            records.addAll(getValuesAtRandomV(i));
        }
        return records;
    }
}
