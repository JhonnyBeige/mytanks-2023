package gtanks.battles.inventory;

import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.effects.Effect;
import gtanks.battles.effects.impl.ArmorEffect;
import gtanks.battles.effects.impl.DamageEffect;
import gtanks.battles.effects.impl.HealthEffect;
import gtanks.battles.effects.impl.Mine;
import gtanks.battles.effects.impl.NitroEffect;
import gtanks.battles.tanks.math.Vector3;
import gtanks.commands.Type;
import gtanks.json.JSONUtils;
import gtanks.logger.Logger;
import gtanks.main.database.DatabaseManager;
import gtanks.main.database.impl.DatabaseManagerImpl;
import gtanks.services.annotations.ServicesInject;
import gtanks.users.garage.items.Item;

public class InventoryController {
   private static final String INIT_INVENTORY_COMAND = "init_inventory";
   private static final String ACTIVATE_ITEM_COMAND = "activate_item";
   private static final String ENABLE_EFFECT_COMAND = "enable_effect";
   @ServicesInject(
      target = DatabaseManagerImpl.class
   )
   private DatabaseManager database = DatabaseManagerImpl.instance();
   private BattlefieldPlayerController player;

   public InventoryController(BattlefieldPlayerController player) {
      this.player = player;
   }

   public void init() {
      this.player.send(Type.BATTLE, "init_inventory", JSONUtils.parseInitInventoryComand(this.player.getGarage()));
   }

   public void activateItem(String id, Vector3 tankPos) {
      Item item = this.player.getGarage().getItemById(id);
      if (item != null && item.count >= 1) {
         Effect effect = this.getEffectById(id);
         if (!this.player.tank.isUsedEffect(effect.getEffectType())) {
            effect.activate(this.player, true, tankPos);
            this.onActivatedItem(item, effect.getDurationTime());
            --item.count;
            if (item.count <= 0) {
               this.player.getGarage().items.remove(item);
            }

            (new Thread(() -> {
               this.player.getGarage().parseJSONData();
               this.database.update(this.player.getGarage());
            })).start();
         }

      }
   }

   private void onActivatedItem(Item item, int durationTime) {
      this.player.send(Type.BATTLE, "activate_item", item.id);
      this.player.battle.sendToAllPlayers(Type.BATTLE, "enable_effect", this.player.getUser().getNickname(), String.valueOf(item.index), String.valueOf(durationTime));
   }

   private Effect getEffectById(String id) {
      Effect effect = null;
      switch(id.hashCode()) {
      case -1221262756:
         if (id.equals("health")) {
            effect = new HealthEffect();
            return (Effect)effect;
         }
         break;
      case -332640355:
         if (id.equals("double_damage")) {
            effect = new DamageEffect();
            return (Effect)effect;
         }
         break;
      case 107371:
         if (id.equals("n2o")) {
            effect = new NitroEffect();
            return (Effect)effect;
         }
         break;
      case 3351635:
         if (id.equals("mine")) {
            effect = new Mine();
            return (Effect)effect;
         }
         break;
      case 93086015:
         if (id.equals("armor")) {
            effect = new ArmorEffect();
            return (Effect)effect;
         }
      }

      Logger.log("Effect with id:" + id + " not found!");
      return (Effect)effect;
   }
}
