package gtanks.exceptions;

import gtanks.logger.Logger;
import gtanks.logger.Type;
import java.util.Arrays;

public class GTanksServerException extends Exception {
   private static final long serialVersionUID = 1L;

   public GTanksServerException(String error) {
      super(error);
      Logger.log(Type.ERROR, "Throw server exception with message: " + error);
   }

   public String toString() {
      return Arrays.toString(super.getStackTrace());
   }
}
