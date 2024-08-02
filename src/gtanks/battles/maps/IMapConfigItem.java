package gtanks.battles.maps;

public class IMapConfigItem {
   public String id;
   public String name;
   public String skyboxId;
   public String ambientSoundId;
   public String gameMode;
   public String themeName;
   public int minRank;
   public int maxRank;
   public int maxPlayers;
   public boolean tdm = false;
   public boolean ctf = false;
   public double angleX;
   public double angleZ;
   public int lightColor;
   public int shadowColor;
   public double fogAlpha;
   public int fogColor;
   public int ssaoColor;

   public IMapConfigItem(String id, String name, String skyboxId, int minRank, int maxRank, int maxPlayers, boolean tdm, boolean ctf, double angleX, double angleZ, int lightColor, int shadowColor, double fogAlpha, int fogColor, int ssaoColor) {
      this.id = id;
      this.name = name;
      this.skyboxId = skyboxId;
      this.minRank = minRank;
      this.maxRank = maxRank;
      this.tdm = tdm;
      this.ctf = ctf;
      this.maxPlayers = maxPlayers;
      this.angleX = angleX;
      this.angleZ = angleZ;
      this.lightColor = lightColor;
      this.shadowColor = shadowColor;
      this.fogAlpha = fogAlpha;
      this.fogColor = fogColor;
      this.ssaoColor = ssaoColor;
   }

   public IMapConfigItem(String id, String name, String skyboxId, int minRank, int maxRank, int maxPlayers, boolean tdm, boolean ctf, double angleX, double angleZ, int lightColor, int shadowColor, double fogAlpha, int fogColor, int ssaoColor, String soundId, String gamemodeId) {
      this.id = id;
      this.name = name;
      this.skyboxId = skyboxId;
      this.minRank = minRank;
      this.maxRank = maxRank;
      this.tdm = tdm;
      this.ctf = ctf;
      this.maxPlayers = maxPlayers;
      this.ambientSoundId = soundId;
      this.gameMode = gamemodeId;
      this.angleX = angleX;
      this.angleZ = angleZ;
      this.lightColor = lightColor;
      this.shadowColor = shadowColor;
      this.fogAlpha = fogAlpha;
      this.fogColor = fogColor;
      this.ssaoColor = ssaoColor;
   }
}
