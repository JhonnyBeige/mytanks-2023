package gtanks.battles.effects.impl;

import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.effects.Effect;
import gtanks.battles.effects.EffectType;
import gtanks.battles.effects.activator.EffectActivatorService;
import gtanks.battles.tanks.math.Vector3;
import gtanks.commands.Type;
import gtanks.json.JSONUtils;
import gtanks.services.annotations.ServicesInject;
import java.util.TimerTask;

public class NitroEffect extends TimerTask implements Effect {
   private static final String CHANGE_TANK_SPEC_COMAND = "change_spec_tank";
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
      synchronized(player.tank.activeEffects) {
         player.tank.activeEffects.add(this);
      }

      player.tank.speed = this.addPercent(player.tank.speed, 30);
      player.battle.sendToAllPlayers(Type.BATTLE, "change_spec_tank", player.tank.id, JSONUtils.parseTankSpec(player.tank, true));
      this.effectActivatorService.activateEffect(this, this.fromInventory ? 60000L : 40000L);
   }

   public void deactivate() {
      this.deactivated = true;
      this.player.tank.activeEffects.remove(this);
      this.player.battle.sendToAllPlayers(Type.BATTLE, "disnable_effect", this.player.getUser().getNickname(), String.valueOf(this.getID()));
      this.player.tank.speed = this.player.tank.getHull().speed;
      this.player.battle.sendToAllPlayers(Type.BATTLE, "change_spec_tank", this.player.tank.id, JSONUtils.parseTankSpec(this.player.tank, true));
   }

   public void run() {
      if (!this.deactivated) {
         this.deactivate();
      }

   }

   public EffectType getEffectType() {
      return EffectType.NITRO;
   }

   public int getID() {
      return 4;
   }

   private float addPercent(float value, int percent) {
      return value / 100.0F * (float)percent + value;
   }

   public int getDurationTime() {
      return 60000;
   }
}
