package gtanks.gui.console;

import gtanks.StringUtils;
import gtanks.services.LobbysServices;
import gtanks.services.annotations.ServicesInject;
import java.awt.Color;

public class ConsoleComandHandler implements IComandHandler {
   private ConsoleWindow context;
   @ServicesInject(
      target = LobbysServices.class
   )
   private LobbysServices lobbyMessages = LobbysServices.getInstance();

   public ConsoleComandHandler(ConsoleWindow context) {
      this.context = context;
   }

   public void onEnterComand(String cmd) {
      String[] args = cmd.split(" ");
      String var3;
      switch((var3 = args[0]).hashCode()) {
      case 110620997:
         if (var3.equals("trace")) {
            this.context.append(Color.YELLOW, StringUtils.concatMassive(args, 1));
         }
      default:
      }
   }
}
