package gtanks.collections;

import gtanks.collections.strings.CaseInsensitiveString;
import java.util.HashMap;

public class CaseInsensitiveMap<T> extends HashMap<CaseInsensitiveString, T> {
   private static final long serialVersionUID = -6234177260678293672L;

   public T put(CaseInsensitiveString key, T obj) {
      return super.put(key, obj);
   }

   public T get(CaseInsensitiveString key) {
      return super.get(key);
   }

   public T remove(CaseInsensitiveString key) {
      return super.remove(key);
   }
}
