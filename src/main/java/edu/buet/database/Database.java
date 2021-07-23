package edu.buet.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database<K,V>{
    private Map<K,V> objects;
    public Database(){
        objects = new ConcurrentHashMap<>();
    }
}
