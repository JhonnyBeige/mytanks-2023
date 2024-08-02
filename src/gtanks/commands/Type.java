package gtanks.commands;

public enum Type {
   AUTH {
      public String toString() {
         return "auth";
      }
   },
   REGISTRATON {
      public String toString() {
         return "registration";
      }
   },
   GARAGE {
      public String toString() {
         return "garage";
      }
   },
   CHAT {
      public String toString() {
         return "chat";
      }
   },
   LOBBY {
      public String toString() {
         return "lobby";
      }
   },
   LOBBY_CHAT {
      public String toString() {
         return "lobby_chat";
      }
   },
   BATTLE {
      public String toString() {
         return "battle";
      }
   },
   PING {
      public String toString() {
         return "ping";
      }
   },
   UNKNOWN {
      public String toString() {
         return "UNKNOWN".toLowerCase();
      }
   },
   HTTP {
      public String toString() {
         return "http";
      }
   },
   SYSTEM {
      public String toString() {
         return "system";
      }
   };

   private Type() {
   }

   // $FF: synthetic method
   Type(Type var3) {
      this();
   }
}
