package gtanks.battles.effects.impl;

import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.effects.Effect;
import gtanks.battles.effects.EffectType;
import gtanks.battles.effects.activator.EffectActivatorService;
import gtanks.battles.tanks.math.Vector3;
import gtanks.commands.Type;
import gtanks.services.annotations.ServicesInject;
import java.util.TimerTask;

public class Mine extends TimerTask implements Effect {
   @ServicesInject(
      target = EffectActivatorService.class
   )
   private static final EffectActivatorService effectActivatorService = EffectActivatorService.getInstance();
   private BattlefieldPlayerController player;
   private Vector3 tankPos;
   private boolean deactivated;

   public void activate(BattlefieldPlayerController player, boolean fromInventory, Vector3 tankPos) {
      if (!fromInventory) {
         throw new IllegalArgumentException("Effect 'Mine' was not caused from inventory!");
      } else {
         this.player = player;
         this.tankPos = tankPos;
         synchronized(player.tank.activeEffects) {
            player.tank.activeEffects.add(this);
         }

         player.battle.battleMinesModel.tryPutMine(player, this.tankPos);
         effectActivatorService.activateEffect(this, (long)this.getDurationTime());
      }
   }

   public void deactivate() {
      this.deactivated = true;
      this.player.tank.activeEffects.remove(this);
      this.player.battle.sendToAllPlayers(Type.BATTLE, "disnable_effect", this.player.getUser().getNickname(), String.valueOf(this.getID()));
   }

   public EffectType getEffectType() {
      return EffectType.MINE;
   }

   public int getID() {
      return 5;
   }

   public int getDurationTime() {
      return 30000;
   }

   public void run() {
      if (!this.deactivated) {
         this.deactivate();
      }

   }
}
