import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    static HashIndex hashIndex;
    static ArrayIndex arrayIndex;

    public static void main(String[] args) throws IOException {

        BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in)
        );

        while (true) {
            System.out.println("Ready for next command...");
            String in = input.readLine();
            //If user calls to create index
            if(in.equals("CREATE INDEX ON Project2Dataset (RandomV)")){
                //initIndexes() returns false if indexes already exist.
                boolean success = initIndexes();
                if(success){
                    System.out.println("The hash-based and array-based indexes are built successfully.");
                }
                else{
                    System.out.println("Indexes have already been built.");
                }
            }
            //Minimum length of all queries, so must be a select command
            else if(in.length() > 45){
                long elapsedTime = 0;
                int filesRead = 0;
                int indexType = 0;
                ArrayList<String> query = new ArrayList<>();

                //key for deciding which type of select command it is
                char key = in.charAt(44);

                //This is a select EQUALS statement
                if(key == '=') {
                    int v = Integer.parseInt(in.substring(46));

                    //Validate v
                    if (v < 1 || v > 5000) {
                        System.out.println("Error: randomV value out of bounds. Expected 1 <= randomV <= 5000");
                        continue;
                    }

                    if (hashIndex == null) {
                        long start = System.currentTimeMillis();
                        query = FileSupport.fullTableScan(v);
                        long stop = System.currentTimeMillis();
                        elapsedTime = stop - start;
                        filesRead = 5000;
                    } else {
                        indexType = 1;
                        long start = System.currentTimeMillis();
                        ArrayList<Integer> filesOpened = new ArrayList<>();
                        ArrayList<Pair> records = hashIndex.getValuesAtRandomV(v);
                        //If a randomV has no records, this will be null. ex. randomV = 10
                        if (records != null) {
                            for (Pair p : records) {
                                String record = FileSupport.readRecord(p.file, p.offset);
                                if (!filesOpened.contains(p.file)) {
                                    filesOpened.add(p.file);
                                }
                                query.add(record);
                            }
                            long stop = System.currentTimeMillis();
                            elapsedTime = stop - start;
                            filesRead = filesOpened.size();
                        }
                    }
                    System.out.println("Records found at randomV = " + v);
                    for (String s : query) {
                        System.out.println(s);
                    }
                    printStats(indexType, elapsedTime, filesRead);
                }
                //This is a select NOT statement
                else if(key == '!'){
                    int v = Integer.parseInt(in.substring(47));

                    //Validate v
                    if(v < 1 || v > 5000){
                        System.out.println("Error: randomV value out of bounds. Expected 1 <= randomV <= 5000");
                        continue;
                    }

                    long start = System.currentTimeMillis();
                    query = FileSupport.fullTableNotScan(v);
                    long stop = System.currentTimeMillis();
                    elapsedTime = stop - start;
                    filesRead = 5000;

                    System.out.println("Records found at randomV != " + v);
                    for(String s : query) {
                        System.out.println(s);
                    }
                    printStats(indexType, elapsedTime, filesRead);
                }
                //This is a select in range statement
                else if(key == '>'){
                    String[] inArr = in.split(" ");
                    int v1 = Integer.parseInt(inArr[7]);
                    int v2 = Integer.parseInt(inArr[11]);

                    //Validate v1 and v2
                    if(v1 < 0 || v1 > 5000){
                        System.out.println("Error: randomV value for v1 out of bounds. Expected 1 <= randomV <= 5000");
                        continue;
                    }
                    if(v2 < 1 || v2 > 5001){
                        System.out.println("Error: randomV value for v2 out of bounds. Expected 1 <= randomV <= 5000");
                        continue;
                    }
                    if(v2 >= v1){
                        System.out.println("Error: v2 is greater than or equal to v1. Expected Expected 1 <= v1 < v2 <= 5000");
                    }

                    //If index was not built do a full table scan
                    if(arrayIndex == null){
                        long start = System.currentTimeMillis();
                        query = FileSupport.fullTableRangeScan(v1, v2);
                        long stop = System.currentTimeMillis();
                        elapsedTime = stop - start;
                        filesRead = 5000;
                    }
                    else{
                        indexType = 2;
                        long start = System.currentTimeMillis();
                        ArrayList<Integer> filesOpened = new ArrayList<>();
                        ArrayList<Pair> records = arrayIndex.getValuesInRange(v1, v2);
                        //If a randomV has no records, this will be null. ex. v1 = 9, v2 = 11
                        if(records != null) {
                            for (Pair p : records) {
                                String record = FileSupport.readRecord(p.file, p.offset);
                                if (!filesOpened.contains(p.file)) {
                                    filesOpened.add(p.file);
                                }
                                query.add(record);
                            }
                            long stop = System.currentTimeMillis();
                            elapsedTime = stop - start;
                            filesRead = filesOpened.size();
                        }

                    }

                    System.out.println("Records found at " + v1 + " < randomV < " + v2);
                    for(String s : query) {
                        System.out.println(s);
                    }
                    printStats(indexType, elapsedTime, filesRead);
                }
            }
            else if(in.toLowerCase().equals("exit")){
                System.out.println("Exiting program...");
                System.exit(1);
            }
        }
    }

    public static boolean initIndexes() throws IOException {
        //If hash is not null neither will array be since they are initialized at the same time
        if(hashIndex != null){
            return false;
        }
        IndexPair indexPair = FileSupport.initBothIndex();

        hashIndex = new HashIndex(indexPair.hashIndex);

        arrayIndex = new ArrayIndex(indexPair.arrayIndex);
        return true;
    }

    /*
    int index Options:
        0 : No Index
        1 : Hash Index
        2 : Array Index
     */
    public static void printStats(int index, long time, int files){
        StringBuilder sb = new StringBuilder();
        sb.append("----------Query Stats----------\n");
        switch(index){
            case 0:
                sb.append("Index: No Index Used\n");
                break;
            case 1:
                sb.append("Index: Hash-based Index Used\n");
                break;
            case 2:
                sb.append("Index: Array-based Index Used\n");
                break;
        }
        sb.append("Elapsed time: " + time + " milliseconds\n");
        sb.append("Files read: " + files + "\n");
        sb.append("--------------------------------");
        System.out.println(sb);
    }
}
