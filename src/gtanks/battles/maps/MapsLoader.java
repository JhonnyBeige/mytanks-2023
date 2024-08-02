package gtanks.battles.maps;

import gtanks.battles.maps.parser.Parser;
import gtanks.battles.maps.parser.map.bonus.BonusRegion;
import gtanks.battles.maps.parser.map.bonus.BonusType;
import gtanks.battles.maps.parser.map.spawn.SpawnPosition;
import gtanks.battles.maps.parser.map.spawn.SpawnPositionType;
import gtanks.battles.maps.themes.MapThemeFactory;
import gtanks.battles.tanks.math.Vector3;
import gtanks.logger.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.bind.JAXBException;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MapsLoader {
   public static HashMap<String, Map> maps = new HashMap();
   private static ArrayList<IMapConfigItem> configItems = new ArrayList();
   private static Parser parser;

   public static void initFactoryMaps() {
      Logger.log("Maps Loader Factory inited. Loading maps...");

      try {
         parser = new Parser();
      } catch (JAXBException var1) {
         var1.printStackTrace();
      }

      loadConfig();
   }

   private static void loadConfig() {
      try {
         JSONParser mapsParser = new JSONParser();
         Object items = mapsParser.parse((Reader)(new FileReader(new File("maps/config.json"))));
         JSONObject obj = (JSONObject)items;
         JSONArray jarray = (JSONArray)obj.get("maps");

         IMapConfigItem __item;
         for(Iterator var5 = jarray.iterator(); var5.hasNext(); configItems.add(__item)) {
            Object objItem = var5.next();
            JSONObject item = (JSONObject)objItem;
            String id = (String)item.get("id");
            String name = (String)item.get("name");
            String skyboxId = (String)item.get("skybox_id");
            Object ambientSoundId = item.get("ambient_sound_id");
            Object gameModeId = item.get("gamemode_id");
            int minRank = Integer.parseInt((String)item.get("min_rank"));
            int maxRank = Integer.parseInt((String)item.get("max_rank"));
            int maxPlayers = Integer.parseInt((String)item.get("max_players"));
            boolean tdm = (Boolean)item.get("tdm");
            boolean ctf = (Boolean)item.get("ctf");
            Object themeId = item.get("theme_id");

            double angleX = Double.parseDouble(String.valueOf(item.get("angleX")));
            double angleZ = Double.parseDouble(String.valueOf(item.get("angleZ")));
            int lightColor = Integer.parseInt(String.valueOf(item.get("lightColor")));
            int shadowColor = Integer.parseInt(String.valueOf(item.get("shadowColor")));
            double fogAlpha = Double.parseDouble(String.valueOf(item.get("fogAlpha")));
            int fogColor = Integer.parseInt(String.valueOf(item.get("fogColor")));
            int ssaoColor = Integer.parseInt(String.valueOf(item.get("ssaoColor")));

            __item = ambientSoundId != null && gameModeId != null ? new IMapConfigItem(id, name, skyboxId, minRank, maxRank, maxPlayers, tdm, ctf, angleX, angleZ, lightColor, shadowColor, fogAlpha, fogColor, ssaoColor, (String)ambientSoundId, (String)gameModeId) : new IMapConfigItem(id, name, skyboxId, minRank, maxRank, maxPlayers, tdm, ctf, angleX, angleZ, lightColor, shadowColor, fogAlpha, fogColor, ssaoColor);
            if (themeId != null) {
               __item.themeName = (String)themeId;
            }
         }

         parseMaps();
      } catch (IOException | ParseException var19) {
         var19.printStackTrace();
      }

   }

   private static void parseMaps() {
      File[] maps = (new File("maps")).listFiles();
      File[] var4 = maps;
      int var3 = maps.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         File file = var4[var2];
         if (!file.isDirectory() && file.getName().endsWith(".xml")) {
            parse(file);
         }
      }

      Logger.log("Loaded all maps!\n");
   }

   private static void parse(File file) {
      Logger.log("Loading " + file.getName() + "...");
      IMapConfigItem temp = getMapItem(file.getName().substring(0, file.getName().length() - 4));
      if (temp != null) {
         Map map = null;

         try {
            map = new Map() {
               {
                  this.name = temp.name;
                  this.id = temp.id;
                  this.skyboxId = temp.skyboxId;
                  this.minRank = temp.minRank;
                  this.maxRank = temp.maxRank;
                  this.maxPlayers = temp.maxPlayers;
                  this.tdm = temp.tdm;
                  this.ctf = temp.ctf;
                  this.angleX = temp.angleX;
                  this.angleZ = temp.angleZ;
                  this.lightColor = temp.lightColor;
                  this.shadowColor = temp.shadowColor;
                  this.fogAlpha = temp.fogAlpha;
                  this.fogColor = temp.fogColor;
                  this.ssaoColor = temp.ssaoColor;
                  this.md5Hash = DigestUtils.md5Hex((InputStream)(new FileInputStream(file)));
                  this.mapTheme = temp.ambientSoundId != null && temp.gameMode != null ? MapThemeFactory.getMapTheme(temp.ambientSoundId, temp.gameMode) : MapThemeFactory.getDefaultMapTheme();
                  this.themeId = temp.themeName;
               }
            };
         } catch (IOException var9) {
            var9.printStackTrace();
         }

         gtanks.battles.maps.parser.map.Map parsedMap = null;

         try {
            parsedMap = parser.parseMap(file);
         } catch (JAXBException var8) {
            var8.printStackTrace();
         }

         Iterator var5 = parsedMap.getSpawnPositions().iterator();

         while(var5.hasNext()) {
            SpawnPosition sp = (SpawnPosition)var5.next();
            if (sp.getSpawnPositionType() == SpawnPositionType.NONE) {
               map.spawnPositonsDM.add(sp.getVector3());
            }

            if (sp.getSpawnPositionType() == SpawnPositionType.RED) {
               map.spawnPositonsRed.add(sp.getVector3());
            }

            if (sp.getSpawnPositionType() == SpawnPositionType.BLUE) {
               map.spawnPositonsBlue.add(sp.getVector3());
            }
         }

         if (parsedMap.getBonusesRegion() != null) {
            var5 = parsedMap.getBonusesRegion().iterator();

            while(var5.hasNext()) {
               BonusRegion br = (BonusRegion)var5.next();
               Iterator var7 = br.getType().iterator();

               while(var7.hasNext()) {
                  BonusType type = (BonusType)var7.next();
                  if (type == BonusType.CRYSTALL) {
                     map.crystallsRegions.add(br.toServerBonusRegion());
                  } else if (type == BonusType.CRYSTALL_100) {
                     map.goldsRegions.add(br.toServerBonusRegion());
                  } else if (type == BonusType.CRYSTALL) {
                     map.crystallsRegions.add(br.toServerBonusRegion());
                  } else if (type == BonusType.ARMOR) {
                     map.armorsRegions.add(br.toServerBonusRegion());
                  } else if (type == BonusType.DAMAGE) {
                     map.damagesRegions.add(br.toServerBonusRegion());
                  } else if (type == BonusType.HEAL) {
                     map.healthsRegions.add(br.toServerBonusRegion());
                  } else if (type == BonusType.NITRO) {
                     map.nitrosRegions.add(br.toServerBonusRegion());
                  }
               }
            }
         }

         map.flagBluePosition = parsedMap.getPositionBlueFlag() != null ? parsedMap.getPositionBlueFlag().toVector3() : null;
         map.flagRedPosition = parsedMap.getPositionRedFlag() != null ? parsedMap.getPositionRedFlag().toVector3() : null;
         if (map.flagBluePosition != null) {
            Vector3 var10000 = map.flagBluePosition;
            var10000.z += 50.0F;
            var10000 = map.flagRedPosition;
            var10000.z += 50.0F;
         }

         maps.put(map.id, map);
      }
   }

   private static IMapConfigItem getMapItem(String id) {
      Iterator var2 = configItems.iterator();

      while(var2.hasNext()) {
         IMapConfigItem item = (IMapConfigItem)var2.next();
         if (item.id.equals(id)) {
            return item;
         }
      }

      return null;
   }
}
