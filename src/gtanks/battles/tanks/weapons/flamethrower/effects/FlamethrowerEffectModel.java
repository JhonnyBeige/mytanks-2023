package gtanks.battles.tanks.weapons.flamethrower.effects;

import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.tanks.Tank;
import gtanks.battles.tanks.weapons.effects.IEffect;
import gtanks.commands.Type;
import gtanks.json.JSONUtils;

public class FlamethrowerEffectModel implements IEffect {
   private float power;
   private Double emc = Double.valueOf(0.0D);
   private Tank tank;
   private BattlefieldModel bfModel;
   private FlamethrowerTimer currFlameTimer;
   private BattlefieldPlayerController targetsTanks;
   private BattlefieldPlayerController player;
   private float damage;

   public FlamethrowerEffectModel(float power, Tank tank, BattlefieldModel bfModel, BattlefieldPlayerController targetsTanks, BattlefieldPlayerController player, float damage) {
      this.power = power;
      this.tank = tank;
      this.bfModel = bfModel;
      this.damage = damage;
      this.targetsTanks = targetsTanks;
      this.player = player;
   }

   public void setStartSpecFromTank() {

   }

   public void update() {
      this.emc = Double.valueOf(this.emc.doubleValue() + 0.2D);
      if (this.currFlameTimer != null) {
         this.currFlameTimer.stoped = true;
      }

      this.currFlameTimer = new FlamethrowerTimer();
      this.currFlameTimer.start();
      this.sendSpecData();
      this.sendChangeTemperature(TemperatureCalc.getTemperature(this.emc.doubleValue()));
   }

   private void sendSpecData() {
      this.bfModel.sendToAllPlayers(Type.BATTLE, "change_spec_tank", this.tank.id, JSONUtils.parseTankSpec(this.tank, false));
   }

   private void sendChangeTemperature(double value) {
      this.bfModel.sendToAllPlayers(Type.BATTLE, "change_temperature_tank", this.tank.id, String.valueOf(value));
   }

   class FlamethrowerTimer extends Thread {
      public boolean stoped = false;

      public void run() {
         try {
            this.setName("FLAME TIMER THREAD " + FlamethrowerEffectModel.this.tank);
            FlamethrowerEffectModel.this.emc = Double.valueOf(FlamethrowerEffectModel.this.emc.doubleValue() - 0.03D);
            try {
               sleep(5500L);
               for (int i = 0; i<=1; i++) {
                  FlamethrowerEffectModel.this.bfModel.tanksKillModel.damageTank(FlamethrowerEffectModel.this.targetsTanks, FlamethrowerEffectModel.this.player, FlamethrowerEffectModel.this.damage * 0.2F, true);
               }
            } catch (InterruptedException var2) {
               var2.printStackTrace();
            }
         } catch (NullPointerException err) {}
         if (!this.stoped) {
            tank.flameEffect = null;
            FlamethrowerEffectModel.this.sendSpecData();
            FlamethrowerEffectModel.this.sendChangeTemperature(0.0D);
         }
      }
   }
}
