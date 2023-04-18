import java.util.ArrayList;


public class ArrayIndex {

    //Array of ArrayLists of Pairs, Each ArrayList is at the index of randomV-1,
    //and is a list of file and record number pairs at that randomV
    ArrayList<Pair>[] index;

    public ArrayIndex(ArrayList<Pair>[] index){
        this.index = index;
    }

    //Array is 0 based indexing, so must minus the randomV by 1 to get proper index
    public ArrayList<Pair> getValuesAtRandomV(int randomV){
        return index[randomV-1];
    }

    public ArrayList<Pair> getValuesInRange(int v1, int v2) {
        ArrayList<Pair> records = new ArrayList<>();
        for(int i = v1+1; i < v2; i++){
            records.addAll(getValuesAtRandomV(i));
        }
        return records;
    }
}
