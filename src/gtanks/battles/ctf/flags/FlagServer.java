package gtanks.battles.ctf.flags;

import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.ctf.FlagReturnTimer;
import gtanks.battles.tanks.math.Vector3;

public class FlagServer {
   public String flagTeamType;
   public BattlefieldPlayerController owner;
   public Vector3 position;
   public Vector3 basePosition;
   public FlagState state;
   public FlagReturnTimer returnTimer;
}
