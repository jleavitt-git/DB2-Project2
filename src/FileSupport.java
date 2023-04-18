import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class FileSupport {

    //Pass through all files once and populate both indexes. Return an IndexPair which contains
    //all data for both indexes.
    public static IndexPair initBothIndex() throws IOException {
        ArrayList<Pair>[] aIndex = new ArrayList[5000];
        HashMap<Integer, ArrayList<Pair>> hIndex = new HashMap<>();

        //Go through every file
        for(int i = 1; i < 100; i++) {
            char[] charFile = getCharArrayFromFile(i);
            //Read 100 records per file
            for(int j = 1; j <= 100; j++){
                int fileNum = i;
                int record = j;
                int randomVOffset = ((j-1)*40) + 33; //Offset to get randomV value
                int randomV = Integer.parseInt(String.copyValueOf(charFile, randomVOffset, 4));
                Pair p = new Pair(fileNum, record);
                if(hIndex.containsKey(randomV)){
                    hIndex.get(randomV).add(p);
                }
                else{
                    hIndex.put(randomV, new ArrayList<>());
                    hIndex.get(randomV).add(p);
                }
                randomV--; //Subtract 1 from randomV because 0 based indexing in the arrayIndex
                if(aIndex[randomV] != null){
                    aIndex[randomV].add(p);
                }
                else{
                    aIndex[randomV] = new ArrayList<>();
                    aIndex[randomV].add(p);
                }
            }
        }

        return new IndexPair(hIndex, aIndex);
    }

    public static ArrayList<String> fullTableScan(int v) throws IOException {
        ArrayList<String> query = new ArrayList<>();

        //Go through every file
        for(int i = 1; i < 100; i++) {
            char[] charFile = getCharArrayFromFile(i);
            //Read 100 records from each file
            for (int j = 1; j <= 100; j++) {
                int randomVOffset = ((j - 1) * 40) + 33; //Offset to get randomV value
                int randomV = Integer.parseInt(String.copyValueOf(charFile, randomVOffset, 4));
                if(randomV == v){
                    query.add(String.copyValueOf(charFile, (j-1)*40, 40));
                }
            }
        }
        return query;
    }

    public static ArrayList<String> fullTableRangeScan(int v1, int v2) throws IOException {
        ArrayList<String> query = new ArrayList<>();

        //Go through every file
        for(int i = 1; i < 100; i++) {
            char[] charFile = getCharArrayFromFile(i);
            //Read 100 records from each file
            for (int j = 1; j <= 100; j++) {
                int randomVOffset = ((j - 1) * 40) + 33; //Offset to get randomV value
                int randomV = Integer.parseInt(String.copyValueOf(charFile, randomVOffset, 4));
                if(randomV > v1 && randomV < v2){
                    query.add(String.copyValueOf(charFile, (j-1)*40, 40));
                }
            }
        }
        return query;
    }

    public static ArrayList<String> fullTableNotScan(int v) throws IOException {
        ArrayList<String> query = new ArrayList<>();

        //Go through every file
        for(int i = 1; i < 100; i++) {
            char[] charFile = getCharArrayFromFile(i);
            //Read 100 records from each file
            for (int j = 1; j <= 100; j++) {
                int randomVOffset = ((j - 1) * 40) + 33; //Offset to get randomV value
                int randomV = Integer.parseInt(String.copyValueOf(charFile, randomVOffset, 4));
                if(randomV != v){
                    query.add(String.copyValueOf(charFile, (j-1)*40, 40));
                }
            }
        }
        return query;
    }

    public static String readRecord(int f, int r) throws IOException {
        //Get data from file
        char[] charFile = getCharArrayFromFile(f);

        StringBuilder sb = new StringBuilder();

        int offset = (r-1)*40;
        //Read 40 characters at the offset to get the full record
        for(int i = offset; i < offset+40; i++){
            sb.append(charFile[i]);
        }
        return sb.toString();
    }

    //Read in entire file and convert from bytes to characters
    private static char[] getCharArrayFromFile(int f) throws IOException {
        byte[] file;
        String fileName = "resources/F" + f + ".txt";
        Path path = Paths.get(fileName);
        file = Files.readAllBytes(path);
        char[] charFile = new char[file.length];
        for (int j = 0; j < file.length; j++) {
            charFile[j] = (char) file[j];
        }
        return charFile;
    }

}
