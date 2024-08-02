package gtanks.lobby.top;

import gtanks.logger.Logger;
import gtanks.users.User;
import java.util.ArrayList;
import java.util.Collection;

public class HallOfFame {
   private static HallOfFame instance = new HallOfFame();
   private ArrayList<User> top = new ArrayList(100);

   public static HallOfFame getInstance() {
      return instance;
   }

   private HallOfFame() {
   }

   public void addUser(User user) {
      Logger.log("User " + user.getNickname() + " has been added to top. " + (this.top.add(user) ? "DONE" : "ERROR"));
   }

   public void removeUser(User user) {
      Logger.log("User " + user.getNickname() + " has been removed of top. " + (this.top.remove(user) ? "DONE" : "ERROR"));
   }

   public void initHallFromCollection(Collection<? extends User> collection) {
      this.top = new ArrayList(collection);
   }

   public ArrayList<User> getData() {
      return this.top;
   }
}
