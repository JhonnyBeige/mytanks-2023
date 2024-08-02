package gtanks.battles.ctf;

import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.ctf.anticheats.CaptureTheFlagAnticheatModel;
import gtanks.battles.ctf.flags.FlagServer;
import gtanks.battles.ctf.flags.FlagState;
import gtanks.battles.tanks.math.Vector3;
import gtanks.commands.Type;
import gtanks.json.JSONUtils;
import gtanks.services.TanksServices;
import gtanks.services.annotations.ServicesInject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CTFModel extends CaptureTheFlagAnticheatModel {
   private BattlefieldModel bfModel;
   private FlagServer blueFlag = new FlagServer();
   private FlagServer redFlag = new FlagServer();
   @ServicesInject(
      target = TanksServices.class
   )
   private TanksServices tanksServices = TanksServices.getInstance();

   public CTFModel(BattlefieldModel bfModel) {
      super(bfModel);
      this.bfModel = bfModel;
      this.blueFlag.flagTeamType = "BLUE";
      this.redFlag.flagTeamType = "RED";
      this.blueFlag.state = FlagState.BASE;
      this.redFlag.state = FlagState.BASE;
      this.blueFlag.position = bfModel.battleInfo.map.flagBluePosition;
      this.blueFlag.basePosition = this.blueFlag.position;
      this.redFlag.position = bfModel.battleInfo.map.flagRedPosition;
      this.redFlag.basePosition = this.redFlag.position;
   }

   public void attemptToTakeFlag(BattlefieldPlayerController taker, String flagTeamType) {
      FlagServer flag = this.getTeamFlag(flagTeamType);
      if (flag.owner == null) {
         if (taker.playerTeamType.equals(flagTeamType)) {
            FlagServer enemyFlag = this.getEnemyTeamFlag(flagTeamType);
            if (flag.state == FlagState.DROPED) {
               this.returnFlag(taker, flag);
               return;
            }

            if (enemyFlag.owner == taker) {
               if (this.onDeliveredFlag(taker, enemyFlag)) {
                  return;
               }

               this.bfModel.sendToAllPlayers(Type.BATTLE, "deliver_flag", taker.playerTeamType, taker.tank.id);
               enemyFlag.state = FlagState.BASE;
               enemyFlag.owner = null;
               taker.flag = null;
               if (enemyFlag.returnTimer != null) {
                  enemyFlag.returnTimer.stop = true;
                  enemyFlag.returnTimer = null;
               }

               int score = (taker.playerTeamType == "BLUE" ? this.bfModel.battleInfo.redPeople : this.bfModel.battleInfo.bluePeople) * 10;
               this.tanksServices.addScore(taker.parentLobby, score);
               taker.statistic.addScore(score);
               this.bfModel.statistics.changeStatistic(taker);
               double fund = 0.0D;
               List<BattlefieldPlayerController> otherTeam = new ArrayList();
               Iterator var10 = this.bfModel.players.iterator();

               BattlefieldPlayerController otherPlayer;
               while(var10.hasNext()) {
                  otherPlayer = (BattlefieldPlayerController)var10.next();
                  if (!otherPlayer.playerTeamType.equals(taker.playerTeamType) && !otherPlayer.playerTeamType.equals("NONE")) {
                     otherTeam.add(otherPlayer);
                  }
               }

               for(var10 = otherTeam.iterator(); var10.hasNext(); fund += Math.sqrt((double)otherPlayer.getUser().getRang() * 0.125D)) {
                  otherPlayer = (BattlefieldPlayerController)var10.next();
               }

               this.bfModel.tanksKillModel.addFund(fund);
               if (taker.playerTeamType == "BLUE") {
                  ++this.bfModel.battleInfo.scoreBlue;
                  this.bfModel.sendToAllPlayers(Type.BATTLE, "change_team_scores", "BLUE", String.valueOf(this.bfModel.battleInfo.scoreBlue));
                  if (this.bfModel.battleInfo.numFlags == this.bfModel.battleInfo.scoreBlue) {
                     this.bfModel.tanksKillModel.restartBattle(false);
                  }
               } else {
                  ++this.bfModel.battleInfo.scoreRed;
                  this.bfModel.sendToAllPlayers(Type.BATTLE, "change_team_scores", "RED", String.valueOf(this.bfModel.battleInfo.scoreRed));
                  if (this.bfModel.battleInfo.numFlags == this.bfModel.battleInfo.scoreRed) {
                     this.bfModel.tanksKillModel.restartBattle(false);
                  }
               }
            }
         } else {
            if (this.onTakeFlag(taker, flag)) {
               return;
            }

            this.bfModel.sendToAllPlayers(Type.BATTLE, "flagTaken", taker.tank.id, flagTeamType);
            flag.state = FlagState.TAKEN_BY;
            flag.owner = taker;
            taker.flag = flag;
            if (flag.returnTimer != null) {
               flag.returnTimer.stop = true;
               flag.returnTimer = null;
            }
         }

      }
   }

   public void dropFlag(BattlefieldPlayerController following, Vector3 posDrop) {
      FlagServer flag = this.getEnemyTeamFlag(following.playerTeamType);
      flag.state = FlagState.DROPED;
      flag.position = posDrop;
      flag.owner = null;
      following.flag = null;
      flag.returnTimer = new FlagReturnTimer(this, flag);
      flag.returnTimer.start();
      this.bfModel.sendToAllPlayers(Type.BATTLE, "flag_drop", JSONUtils.parseDropFlagCommand(flag));
   }

   public void returnFlag(BattlefieldPlayerController following, FlagServer flag) {
      flag.state = FlagState.BASE;
      if (flag.owner != null) {
         flag.owner.flag = null;
         flag.owner = null;
      }

      flag.position = flag.basePosition;
      if (flag.returnTimer != null) {
         flag.returnTimer.stop = true;
         flag.returnTimer = null;
      }

      String id = following == null ? null : following.tank.id;
      this.bfModel.sendToAllPlayers(Type.BATTLE, "return_flag", flag.flagTeamType, id);
      int score = 5;
      if (following != null) {
         this.tanksServices.addScore(following.parentLobby, score);
         following.statistic.addScore(score);
         this.bfModel.statistics.changeStatistic(following);
      }

   }

   private FlagServer getTeamFlag(String teamType) {
      if (teamType.equals("BLUE")) {
         return this.blueFlag;
      } else {
         return teamType.equals("RED") ? this.redFlag : null;
      }
   }

   private FlagServer getEnemyTeamFlag(String teamType) {
      if (teamType.equals("BLUE")) {
         return this.redFlag;
      } else {
         return teamType.equals("RED") ? this.blueFlag : null;
      }
   }

   public FlagServer getRedFlag() {
      return this.redFlag;
   }

   public FlagServer getBlueFlag() {
      return this.blueFlag;
   }
}
