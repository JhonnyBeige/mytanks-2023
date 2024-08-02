package gtanks.battles.effects.impl;

import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.effects.Effect;
import gtanks.battles.effects.EffectType;
import gtanks.battles.effects.activator.EffectActivatorService;
import gtanks.battles.tanks.math.Vector3;
import gtanks.commands.Type;
import gtanks.services.annotations.ServicesInject;
import java.util.TimerTask;

public class ArmorEffect extends TimerTask implements Effect {
   private static final long INVENTORY_TIME_ACTION = 60000L;
   private static final long DROP_TIME_ACTION = 40000L;
   @ServicesInject(
      target = EffectActivatorService.class
   )
   private EffectActivatorService effectActivatorService = EffectActivatorService.getInstance();
   private BattlefieldPlayerController player;
   private boolean fromInventory;
   private boolean deactivated;

   public void activate(BattlefieldPlayerController player, boolean fromInventory, Vector3 tankPos) {
      this.fromInventory = fromInventory;
      this.player = player;
      player.tank.activeEffects.add(this);
      this.effectActivatorService.activateEffect(this, this.fromInventory ? 60000L : 40000L);
   }

   public void deactivate() {
      this.deactivated = true;
      this.player.tank.activeEffects.remove(this);
      this.player.battle.sendToAllPlayers(Type.BATTLE, "disnable_effect", this.player.getUser().getNickname(), String.valueOf(this.getID()));
   }

   public void run() {
      if (!this.deactivated) {
         this.deactivate();
      }

   }

   public EffectType getEffectType() {
      return EffectType.ARMOR;
   }

   public int getID() {
      return 2;
   }

   public int getDurationTime() {
      return 60000;
   }
}
