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
            if(in.equals("CREATE INDEX ON Project2Dataset (RandomV)")){
                boolean success = initIndexes();
                if(success){
                    System.out.println("The hash-based and array-based indexes are built successfully.");
                }
                else{
                    System.out.println("Indexes have already been built.");
                }
            }
            else if(in.length() > 45){
                long elapsedTime = 0;
                int filesRead = 0;
                int indexType = 0;

                char key = in.charAt(44);
                ArrayList<String> query = new ArrayList<>();

                if(key == '='){
                    int v = Integer.parseInt(in.substring(46));
                    if(v < 1 || v > 5000){
                        System.out.println("Error: randomV value out of bounds. Expected 1 >= randomV >= 5000");
                        continue;
                    }

                    if(hashIndex == null){
                        long start = System.currentTimeMillis();
                        query = FileSupport.fullTableScan(v);
                        long stop = System.currentTimeMillis();
                        elapsedTime = stop - start;
                        filesRead = 5000;
                    }
                    else{
                        indexType = 1;
                        long start = System.currentTimeMillis();
                        ArrayList<Integer> filesOpened = new ArrayList<>();
                        ArrayList<Pair> records = hashIndex.getValuesAtRandomV(v);
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
                    System.out.println("Records found at randomV = " + v);
                    for(String s : query) {
                        System.out.println(s);
                    }
                    printStats(indexType, elapsedTime, filesRead);
                }
                else if(key == '!'){
                    int v = Integer.parseInt(in.substring(47));
                    if(v < 1 || v > 5000){
                        System.out.println("Error: randomV value out of bounds. Expected 1 >= randomV >= 5000");
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
                else if(key == '>'){
                    String[] inArr = in.split(" ");
                    int v1 = Integer.parseInt(inArr[7]);
                    int v2 = Integer.parseInt(inArr[11]);
                    if(v1 < 0 || v1 > 5000){
                        System.out.println("Error: randomV value for v1 out of bounds. Expected 1 >= randomV >= 5000");
                        continue;
                    }
                    if(v2 < 1 || v2 > 5001){
                        System.out.println("Error: randomV value for v2 out of bounds. Expected 1 >= randomV >= 5000");
                        continue;
                    }

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
                        ArrayList<Pair> records = arrayIndex.getValuesinRange(v1, v2);
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
        if(hashIndex != null){
            return false;
        }
        IndexPair indexPair = FileSupport.initBothIndex();

        hashIndex = new HashIndex(indexPair.hashIndex);

        arrayIndex = new ArrayIndex(indexPair.arrayIndex);
        return true;
    }

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
