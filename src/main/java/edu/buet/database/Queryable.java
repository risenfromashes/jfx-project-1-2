package edu.buet.database;

import java.util.List;
import java.util.function.Predicate;

public interface Queryable<V>{
    public Queryable<V> select(String field);
    public Queryable<V> between(String field, Object l, Object r);
    public Queryable<V> max(String field);
    public boolean exists(String field, Object value);
    public Queryable<V> where(Predicate<V> p);
    public List<V> toList();
}
