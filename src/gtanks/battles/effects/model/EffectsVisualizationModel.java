package gtanks.battles.effects.model;

import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.effects.Effect;
import gtanks.battles.spectator.SpectatorController;
import gtanks.commands.Type;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class EffectsVisualizationModel {
   private BattlefieldModel bfModel;

   public EffectsVisualizationModel(BattlefieldModel bfModel) {
      this.bfModel = bfModel;
   }

   public void sendInitData(BattlefieldPlayerController player) {
      JSONObject _obj = new JSONObject();
      JSONArray array = new JSONArray();
      Iterator var5 = this.bfModel.players.values().iterator();

      while(true) {
         BattlefieldPlayerController _player;
         do {
            if (!var5.hasNext()) {
               _obj.put("effects", array);
               player.send(Type.BATTLE, "init_effects", _obj.toJSONString());
               return;
            }

            _player = (BattlefieldPlayerController)var5.next();
         } while(player == _player);

         synchronized(_player.tank.activeEffects) {
            Iterator var8 = _player.tank.activeEffects.iterator();

            while(var8.hasNext()) {
               Effect effect = (Effect)var8.next();
               JSONObject obj = new JSONObject();
               obj.put("userID", _player.getUser().getNickname());
               obj.put("itemIndex", effect.getID());
               obj.put("durationTime", 60000);
               array.add(obj);
            }
         }
      }
   }

   public void sendInitData(SpectatorController player) {
      JSONObject _obj = new JSONObject();
      JSONArray array = new JSONArray();
      Iterator var5 = this.bfModel.players.values().iterator();

      while(var5.hasNext()) {
         BattlefieldPlayerController _player = (BattlefieldPlayerController)var5.next();
         synchronized(_player.tank.activeEffects) {
            Iterator var8 = _player.tank.activeEffects.iterator();

            while(var8.hasNext()) {
               Effect effect = (Effect)var8.next();
               JSONObject obj = new JSONObject();
               obj.put("userID", _player.getUser().getNickname());
               obj.put("itemIndex", effect.getID());
               obj.put("durationTime", 60000);
               array.add(obj);
            }
         }
      }

      _obj.put("effects", array);
      player.sendCommand(Type.BATTLE, "init_effects", _obj.toJSONString());
   }
}
