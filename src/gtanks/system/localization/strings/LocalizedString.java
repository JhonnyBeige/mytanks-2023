package gtanks.system.localization.strings;

import gtanks.system.localization.Localization;
import java.util.HashMap;
import java.util.Map;

public class LocalizedString {
   private Map<Localization, String> localizatedMap = new HashMap();

   protected LocalizedString(String ruVersion, String enVersion) {
      this.localizatedMap.put(Localization.RU, ruVersion);
      this.localizatedMap.put(Localization.EN, enVersion);
   }

   public String localizatedString(Localization loc) {
      String string = (String)this.localizatedMap.get(loc);
      return string == null ? "null" : string;
   }
}
