package gtanks.network.requests;

import gtanks.StringUtils;
import gtanks.auth.Auth;
import gtanks.commands.Command;
import gtanks.commands.Commands;
import gtanks.commands.Type;
import gtanks.lobby.LobbyManager;
import gtanks.logger.Logger;
import gtanks.main.netty.ProtocolTransfer;
import gtanks.network.Networker;
import java.net.Socket;
import org.jboss.netty.channel.ChannelHandlerContext;

/** @deprecated */
@Deprecated
public class RequestsTransfer extends Networker implements Runnable {
   private boolean work = true;
   private StringBuffer inputRequest;
   private StringBuffer badRequest = new StringBuffer();
   public LobbyManager lobby;
   private Auth auth;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$gtanks$commands$Type;

   public RequestsTransfer(Socket client) {
      super(client);
   }

   public void run() {
      while(true) {
         try {
            if (this.work && super.bytes != -1) {
               if ((this.inputRequest = new StringBuffer(this.onCommand().trim())).length() <= 0) {
                  continue;
               }

               if (this.inputRequest.toString().endsWith("~")) {
                  this.inputRequest = new StringBuffer(StringUtils.concatStrings(this.badRequest.toString(), this.inputRequest.toString()));
                  this.parseInputRequest();
                  this.badRequest = new StringBuffer();
                  continue;
               }

               this.badRequest = new StringBuffer(StringUtils.concatStrings(this.badRequest.toString(), this.inputRequest.toString()));
               continue;
            }

            Logger.log("User " + super.socketToString() + " has been disconnected.");
            this.lobby.onDisconnect();
         } catch (Exception var2) {
            this.work = false;
            if (this.lobby != null) {
               this.lobby.onDisconnect();
            }
         }

         return;
      }
   }

   private void parseInputRequest() {
      String[] commands = this.inputRequest.toString().split("~");
      String[] var5 = commands;
      int var4 = commands.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         String request = var5[var3];
         this.sendCommandToManagers(Commands.decrypt(request));
      }

   }

   private void sendCommandToManagers(Command cmd) {
      if (this.auth == null) {
         this.auth = new Auth((ProtocolTransfer)null, (ChannelHandlerContext)null);
      }

      switch($SWITCH_TABLE$gtanks$commands$Type()[cmd.type.ordinal()]) {
      case 1:
         this.auth.executeCommand(cmd);
         break;
      case 2:
         this.auth.executeCommand(cmd);
         break;
      case 3:
         this.lobby.executeCommand(cmd);
         break;
      case 4:
         this.lobby.executeCommand(cmd);
         break;
      case 5:
         this.lobby.executeCommand(cmd);
         break;
      case 6:
         this.lobby.executeCommand(cmd);
         break;
      case 7:
         this.lobby.executeCommand(cmd);
         break;
      case 8:
         this.lobby.executeCommand(cmd);
         break;
      case 9:
         Logger.log("User " + this.socketToString() + " send unknowed request: " + cmd.toString());
      case 10:
      }

   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$gtanks$commands$Type() {
      int[] var10000 = $SWITCH_TABLE$gtanks$commands$Type;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Type.values().length];

         try {
            var0[Type.AUTH.ordinal()] = 1;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[Type.BATTLE.ordinal()] = 7;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[Type.CHAT.ordinal()] = 4;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[Type.GARAGE.ordinal()] = 3;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[Type.HTTP.ordinal()] = 10;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[Type.LOBBY.ordinal()] = 5;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[Type.LOBBY_CHAT.ordinal()] = 6;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[Type.PING.ordinal()] = 8;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Type.REGISTRATON.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Type.SYSTEM.ordinal()] = 11;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Type.UNKNOWN.ordinal()] = 9;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$gtanks$commands$Type = var0;
         return var0;
      }
   }
}
