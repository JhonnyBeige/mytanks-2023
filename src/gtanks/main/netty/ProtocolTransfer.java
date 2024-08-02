package gtanks.main.netty;

import gtanks.StringUtils;
import gtanks.auth.Auth;
import gtanks.commands.Command;
import gtanks.commands.Commands;
import gtanks.commands.Type;
import gtanks.lobby.LobbyManager;
import gtanks.logger.Logger;
import java.io.IOException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

public class ProtocolTransfer {
   private static final String SPLITTER_CMDS = "end~";
   private StringBuffer inputRequest;
   private StringBuffer badRequest = new StringBuffer();
   public LobbyManager lobby;
   public Auth auth;
   private Channel channel;
   private ChannelHandlerContext context;
   public int num = 1;
   private int _lastKey = 1;
   private final int[] _keys = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$gtanks$commands$Type;

   public ProtocolTransfer(Channel channel, ChannelHandlerContext context) {
      this.channel = channel;
      this.context = context;
   }

   public void decryptProtocol(String protocol) {
      if ((this.inputRequest = new StringBuffer(protocol)).length() > 0) {
         if (this.inputRequest.toString().endsWith("end~")) {
            this.inputRequest = new StringBuffer(StringUtils.concatStrings(this.badRequest.toString(), this.inputRequest.toString()));
            String[] var5;
            int var4 = (var5 = this.parseCryptRequests()).length;

            for(int var3 = 0; var3 < var4; ++var3) {
               String request = var5[var3];

               int key;
               try {
                  key = Integer.parseInt(String.valueOf(request.charAt(0)));
               } catch (Exception var8) {
                  Logger.log("[EXCEPTION] Detected cheater(replace protocol): " + this.channel.toString());
                  NettyUsersHandler.block(this.channel.getRemoteAddress().toString().split(":")[0]);
                  this.closeConnection();
                  return;
               }

               if (key == this._lastKey) {
                  Logger.log("Detected cheater(replace protocol): " + this.channel.toString());
                  NettyUsersHandler.block(this.channel.getRemoteAddress().toString().split(":")[0]);
                  this.closeConnection();
                  return;
               }

               int nextKey = (this._lastKey + 1) % this._keys.length;
               if (key != (nextKey == 0 ? 1 : nextKey)) {
                  Logger.log("[NOT QUEQUE KEY " + nextKey + " " + this._lastKey + "] Detected cheater(replace protocol): " + this.channel.toString());
                  NettyUsersHandler.block(this.channel.getRemoteAddress().toString().split(":")[0]);
                  this.closeConnection();
                  return;
               }

               this.inputRequest = new StringBuffer(this.decrypt(request.substring(1, request.length()), key));
               this.sendRequestToManagers(this.inputRequest.toString());
            }

            this.badRequest = new StringBuffer();
         } else {
            this.badRequest = new StringBuffer(StringUtils.concatStrings(this.badRequest.toString(), this.inputRequest.toString()));
         }
      }

   }

   private String[] parseCryptRequests() {
      return this.inputRequest.toString().split("end~");
   }

   private String decrypt(String request, int key) {
      this._lastKey = key;
      char[] _chars = request.toCharArray();

      for(int i = 0; i < request.length(); ++i) {
         _chars[i] = (char)(_chars[i] - (key + this.num));
      }

      System.out.println(_chars);
      return new String(_chars);
   }

   private void sendRequestToManagers(String request) {
      this.sendCommandToManagers(Commands.decrypt(request));
   }

   private void sendCommandToManagers(Command cmd) {
      if (this.auth == null) {
         this.auth = new Auth(this, this.context);
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
         this.auth.executeCommand(cmd);
         break;
      case 9:
         Logger.log("User " + this.channel.toString() + " send unknowed request: " + cmd.toString());
      case 10:
      default:
         break;
      case 11:
         if (this.auth != null) {
            this.auth.executeCommand(cmd);
         }

         if (this.lobby != null) {
            this.lobby.executeCommand(cmd);
         }
      }

   }

   public boolean send(Type type, String... args) throws IOException {
      StringBuilder request = new StringBuilder();
      request.append(type.toString());
      request.append(";");

      for(int i = 0; i < args.length - 1; ++i) {
         request.append(StringUtils.concatStrings(args[i], ";"));
      }

      request.append(StringUtils.concatStrings(args[args.length - 1], "end~"));
      if (this.channel.isWritable() && this.channel.isConnected() && this.channel.isOpen()) {
         this.channel.write(request.toString());
      }

      request = null;
      return true;
   }

   protected void onDisconnect() {
      if (this.lobby != null) {
         this.lobby.onDisconnect();
      }

   }

   public void closeConnection() {
      this.channel.close();
   }

   public String getIP() {
      return this.channel.getRemoteAddress().toString();
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
