package main;

import java.lang.reflect.Array;
import java.util.Objects;

public class MyHashMapOld<T extends Number & Comparable<T>>{
    private int sizeHashTable;
    public T[] hashTable;
    private boolean[] markUsedFields;
    private final HashVariant hashVariant;
    private int startingPosition;

    public enum HashVariant {linearProbing, quadraticProbing, doubleHashing};

    public MyHashMapOld(Class<T> keyType, int sizeHashTable, HashVariant hashVariant) {
        this.sizeHashTable = sizeHashTable;
        this.hashVariant = hashVariant;
        hashTable = (T[]) Array.newInstance(keyType, sizeHashTable);
        initHashTable();
        markUsedFields = new boolean[sizeHashTable];
    }

    private void initHashTable() {
        for (int i = 0; i < sizeHashTable; i++) {
            hashTable[i] = null;
        }
    }

    private void initUsedFieldsArray() {
        for (int i = 0; i < sizeHashTable; i++) {
            markUsedFields[i] = false;
        }
    }

    public int getSizeHashTable() {
        return sizeHashTable;
    }

    public void setSizeHashTable(int sizeHashTable) {
        this.sizeHashTable = sizeHashTable;
    }

    public T[] getHashTable() {
        return hashTable;
    }

    public void setHashTable(T[] hashTable) {
        this.hashTable = hashTable;
    }

    private int hashLinearProbing(T key, int numOfTrys) {
        int value = key.intValue() + numOfTrys;
        return value % getSizeHashTable();
    }

    private int hashQuadraticProbing(T key, int numOfTrys) {
        int firstOrderKoeffizient = 0;
        int secondOrderKoeffizient = 1;
        int value = firstOrderKoeffizient * numOfTrys + secondOrderKoeffizient * numOfTrys * numOfTrys;
        return (key.intValue() + value) % sizeHashTable;
    }

    private int doubleHashing(T key, int numOfTrys) {
        int secondModuloValue = 5;
        return (key.intValue() + numOfTrys * (key.intValue() % secondModuloValue)) % sizeHashTable;
    }

    public void insert(T[] keys) {
        for (T key : keys) {
            insert(key);
        }
    }

    public void insert(T key) {
        int numOfTrys = 0;
        int position = hashKey(key,0);
        int firstPosition = hashKey(key, 0);
        boolean gotInserted = false;

        do {
            if (isEmpty(position)) {
                hashTable[position] = key;
                gotInserted = true;
            }
            else {
                numOfTrys++;
                position = hashKey(key, numOfTrys);

            }
        } while (!gotInserted && position != firstPosition);
    }

    public boolean contains(T key) {
        int numOfTrys = 0;
        return containsRecursion(key, numOfTrys);
    }

    private boolean containsRecursion(T key, int numOfTrys) {
        int position = hashKey(key, numOfTrys);
        if (Objects.equals(hashTable[position], key)) {
            // Base case:
            return true;
        } else if (hashTable[position] != null) {
            // Recursion case:
            return containsRecursion(key, numOfTrys + 1);
        }
        else {
            return false;
        }
    }

    public void delete(T key) {
        int numOfTrys = 0;
        startingPosition = hashKey(key, 0);
        deleteRecursion(key,numOfTrys,true);
    }

    private void deleteRecursion(T key, int numOfTrys, boolean firstCall) {
        int position = hashKey(key, numOfTrys);
        boolean wasFieldUsed = markUsedFields[position];

        if (Objects.equals(hashTable[position], key)) {
            // Base case: Key found -> delete it
            hashTable[position] = null;
            markUsedFields[position] = true;
        } else if ((hashTable[position] != null || (hashTable[position] == null && wasFieldUsed) && position != startingPosition)
                    && position != startingPosition) {
            // Recursion case:
            deleteRecursion(key, numOfTrys + 1,false);
        }
        else {
            // Edge case: Key not in the hashmap
            System.out.println("Hashtable doesn't contain the key: " + key);
        }
    }

    private int hashKey(T key, int numOfTrys) {
        return switch (hashVariant) {
            case linearProbing -> hashLinearProbing(key, numOfTrys);
            case quadraticProbing -> hashQuadraticProbing(key, numOfTrys);
            case doubleHashing -> doubleHashing(key, numOfTrys);
        };
    }

    private boolean isEmpty(int position) {
        if (hashTable[position] == null) {
            return true;
        }
        else {
            return false;
        }
    }
}
