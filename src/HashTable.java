import java.lang.reflect.Array;
import java.util.Stack;

/**
 * Class represents the Hash Table
 *
 * @author Brandon Potts
 * @version November 10, 2015
 */
public class HashTable<T>{

    // array that will act as the table
    private T [] table;
    // holds the current size of the array
    private int fillCount;
    // 70% mark of the table size
    private int resizeCount;
    // holds the size of the table
    private int tableSize;
    // Holds the type of class for the table
    private Class genericClass;


    /****
     * function instantiates the class
     *
     * @param tClass is the class that will be
     * @param tableSize size that when reached will begin
     *                 array resize and reallocation
     */
    @SuppressWarnings("unchecked")
    public HashTable(Class<T> tClass , int tableSize){

        table = (T[]) Array.newInstance(tClass, tableSize);
        fillCount = 0;
        genericClass = tClass;
        resizeCount = (int)Math.floor(tableSize * .70);
        this.tableSize = tableSize;
    }


    /***
     * Returns the fillCount of the table
     * @return int fillCount of the table
     */
    public int getFillCount(){
        return fillCount;
    }


    /***
     * Returns the size of the table
     * @return size of the table
     */
    public int getTableSize(){
        return tableSize;
    }


    /***
     * Inserts a record into the hash table
     * @param insertRecord record that will be inserted
     */
    @SuppressWarnings("unchecked")
    public int insert(T insertRecord){

        // Checks to see if insertRecord is a GISRecord
        if(insertRecord instanceof HashTuple){

            int probeSequenceCount;
            HashTuple tuple = (HashTuple)insertRecord;
            int hashIndex = tuple.getRecord().hashCode() % this.tableSize;

            // Will be true when the index is empty
            if(table[hashIndex] == null){
                table[hashIndex] = (T)tuple;
                fillCount++;
                return 0;
                // Begin prob sequence
            } else{

                int sequence = 1;
                int probeIndex;
                boolean found = false;

                // Will run until a location is found
                while(!found){

                    probeIndex = (hashIndex + computeStepSize(sequence)) % this.tableSize;
                    // Will be true when we've finally found a location
                    if(table[probeIndex] == null){
                        found = true;
                        table[probeIndex] = (T)tuple;
                    } else {
                        sequence++;
                    }
                }
                probeSequenceCount = sequence;
            }
            fillCount++;
            // Checks to see if it's time to resize the array
            if(fillCount >= resizeCount){
                reSizeAndRehash();
            }

            return probeSequenceCount;
        }

        return 0;
    }

    /****
     * Checks to see if a given record exists in the database
     * @param record record that will be checked to see if exits
     * @return record if found
     *         null if the record wasn't found
     */
    @SuppressWarnings("unchecked")
    public Stack<T> find(T record){
        // Checks to see if the record is a hashTuple
        if(record instanceof HashTuple){

            HashTuple tuple = (HashTuple) record;
            GISRecord gRecord = tuple.getRecord();
            Stack<T> returnStack = new Stack<>();
            int hashIndex = gRecord.hashCode() % this.tableSize;

            // Will be true when the index doesn't exist
            if(this.table[hashIndex] == null) {
                return null;
                // Will be true when the records are equal
            }else if (this.table[hashIndex].equals(record)){
                HashTuple foundTuple = (HashTuple)this.table[hashIndex];
                returnStack.push((T)foundTuple);
                int sequence = 1;
                int probeIndex;
                boolean done = false;
                T returnValue;
                // Will run until a location is found
                while(!done){
                    probeIndex = (hashIndex + computeStepSize(sequence)) % this.tableSize;
                    // Will be true when we've finally found a location
                    if(table[probeIndex] == null){
                        done = true;
                    } else {

                        HashTuple testTuple = (HashTuple)table[probeIndex];
                        // Will be true when the records are equal
                        if(testTuple.equals(tuple)){
                            returnValue = record;
                            returnStack.push((T)testTuple);
                        }
                        sequence++;
                    }
                }
                return returnStack;
                // Begins the prob sequence
            } else {
                int sequence = 1;
                int probeIndex;
                boolean found = false;
                T returnValue;
                // Will run until a location is found
                while(!found){

                    probeIndex = (hashIndex + computeStepSize(sequence)) % this.tableSize;
                    // Will be true when we've finally found a location
                    if(table[probeIndex] == null){
                        found = true;
                    } else {

                        HashTuple testTuple = (HashTuple)table[probeIndex];
                        // Will be true when the records are equal
                        if(testTuple.equals(tuple)){
                            returnValue = record;
                            returnStack.push((T)testTuple);
                            found = true;
                        }
                        sequence++;
                    }
                }
                return returnStack;
            }
        }
        return null;
    }

    /***
     * Prints the contents of the array
     * @return contents of the Array
     */
    public String buildArrayContents(){

        String returnArray = "";

        for(int i = 0; i < table.length; i++){
            if(table[i] != null){
                HashTuple tuple = (HashTuple)table[i];
                returnArray += i + ": " + tuple.toString() + "\n";
            }
        }

        return returnArray;
    }

    /****
     * Deletes a record from the HashTable
     * @param record record that will be deleted
     * @return record that was deleted
     *         null if the record wasn't found in the database
     */
    public T delete(T record){
        return null;
    }


    /***
     * Calculates the next value in the probe sequence
     * @param inputValue value that will be used in the formula
     * @return next value in the probe sequence
     */
    private int computeStepSize(int inputValue){

        return ((inputValue * inputValue)  + inputValue) / 2; 
    }

    /****
     * Resizes the array and rehashes the array
     */
    @SuppressWarnings("unchecked")
    private void reSizeAndRehash(){

        Stack<T> stack = new Stack<>();
        // Pushes all the records onto the stack
        for(int i = 0; i < tableSize; i++){

            if(table[i] != null){
                stack.push(table[i]);
            }
        }

//        Class currentClass = getClass();
        this.tableSize =  this.tableSize  * 2;
        this.table = (T[]) Array.newInstance(genericClass, this.tableSize);
        this.fillCount = 0;
        this.resizeCount = (int)Math.floor(tableSize * .70);

        // Inserts the values back into the table
        while(!stack.empty()){
            insert(stack.pop());
        }
    }


}