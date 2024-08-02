package gtanks.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class FastHashMap<K, V> implements Iterable<V> {
   private ConcurrentHashMap<K, V> map = new ConcurrentHashMap();

   public void put(K key, V value) {
      if (key != null && value != null) {
         this.map.put(key, value);
      }
   }

   public void remove(Object key) {
      if (key != null) {
         this.map.remove(key);
      }
   }

   public void remove(Object key, Object value) {
      if (key != null && value != null) {
         this.map.remove(key, value);
      }
   }

   public int size() {
      return this.map.size();
   }

   public void clear() {
      this.map.clear();
   }

   public V get(Object key) {
      V value = null;
      if (key != null && this.map.containsKey(key)) {
         value = this.map.get(key);
      }

      return value;
   }

   public boolean containsKey(Object key) {
      return key == null ? false : this.map.containsKey(key);
   }

   public Collection<V> values() {
      return this.map.values();
   }

   public Set<Entry<K, V>> entys() {
      return this.map.entrySet();
   }

   public Iterator<V> iterator() {
      return this.map.values().iterator();
   }
}
