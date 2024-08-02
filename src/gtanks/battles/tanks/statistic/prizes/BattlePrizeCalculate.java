package gtanks.battles.tanks.statistic.prizes;

import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.tanks.statistic.PlayerStatistic;
import gtanks.services.TanksServices;
import gtanks.services.annotations.ServicesInject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class BattlePrizeCalculate {
   @ServicesInject(
      target = TanksServices.class
   )
   private static TanksServices tankServices = TanksServices.getInstance();

   public static void calc(List<BattlefieldPlayerController> users, int fund) {
      if (users != null && users.size() != 0) {
         BattlefieldPlayerController _first = (BattlefieldPlayerController)Collections.max(users, new Comparator<BattlefieldPlayerController>() {
            public int compare(BattlefieldPlayerController o1, BattlefieldPlayerController o2) {
               return (int)(o1.statistic.getScore() - o2.statistic.getScore());
            }
         });
         PlayerStatistic first = _first.statistic;
         double sumSquare = 0.0D;
         int countFirstUsers = 0;
         Iterator var8 = users.iterator();

         while(var8.hasNext()) {
            BattlefieldPlayerController user = (BattlefieldPlayerController)var8.next();
            long value = user.statistic.getScore();
            if (value != first.getScore()) {
               sumSquare += (double)(value * value);
            } else {
               ++countFirstUsers;
            }
         }

         sumSquare += (double)(first.getScore() * first.getScore() * (long)countFirstUsers * (long)countFirstUsers);
         int allSum = 0;
         Iterator var15 = users.iterator();

         while(var15.hasNext()) {
            BattlefieldPlayerController user = (BattlefieldPlayerController)var15.next();
            if (user.statistic.getScore() != first.getScore()) {
               int prize = (int)((double)((long)fund * user.statistic.getScore() * user.statistic.getScore()) / sumSquare);
               if (prize < 0) {
                  prize = Math.abs(prize);
               }

               allSum += prize;
               user.statistic.setPrize(prize);
               tankServices.addCrystall(user.parentLobby, prize);
            }
         }

         int delta = (fund - allSum) / countFirstUsers;
         Iterator var17 = users.iterator();

         while(var17.hasNext()) {
            BattlefieldPlayerController user = (BattlefieldPlayerController)var17.next();
            PlayerStatistic _user = user.statistic;
            if (_user.getScore() == first.getScore() && user != _first) {
               _user.setPrize(delta);
               tankServices.addCrystall(user.parentLobby, delta);
               allSum += delta;
            }
         }

         first.setPrize(first.getPrize() + (fund - allSum));
         tankServices.addCrystall(_first.parentLobby, first.getPrize());
      }
   }

   public static void calculateForTeam(ArrayList<BattlefieldPlayerController> redUsers, ArrayList<BattlefieldPlayerController> blueUsers, int scoreRed, int scoreBlue, double looseKoeff, int fund) {
      ArrayList usersWin;
      ArrayList usersLoose;
      int prizeWin;
      int prizeLoose;
      if (scoreRed != scoreBlue) {
         int scoreWin = Math.max(scoreRed, scoreBlue);
         int scoreLoose = Math.min(scoreRed, scoreBlue);
         prizeLoose = (int)((double)fund * looseKoeff * (double)scoreLoose / (double)scoreWin);
         prizeWin = fund - prizeLoose;
         usersWin = scoreRed > scoreBlue ? redUsers : blueUsers;
         usersLoose = scoreRed > scoreBlue ? blueUsers : redUsers;
      } else {
         prizeLoose = (int)Math.ceil((double)((float)fund / 2.0F));
         prizeWin = (int)Math.ceil((double)((float)fund / 2.0F));
         usersWin = redUsers;
         usersLoose = blueUsers;
      }

      calc(usersWin, prizeWin);
      calc(usersLoose, prizeLoose);
   }
}
