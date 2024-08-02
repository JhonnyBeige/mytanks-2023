package gtanks.rmi.payments.impl;

import gtanks.lobby.LobbyManager;
import gtanks.logger.Logger;
import gtanks.main.database.DatabaseManager;
import gtanks.main.database.impl.DatabaseManagerImpl;
import gtanks.rmi.payments.RMIPaymentCallback;
import gtanks.rmi.payments.mapping.Payment;
import gtanks.services.LobbysServices;
import gtanks.services.TanksServices;
import gtanks.services.annotations.ServicesInject;
import gtanks.users.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIPaymentCallbackImpl extends UnicastRemoteObject implements RMIPaymentCallback {
   private static final long serialVersionUID = 13322234112L;
   private static final int RUB_COURSE = 3;
   @ServicesInject(
      target = DatabaseManagerImpl.class
   )
   private DatabaseManager database = DatabaseManagerImpl.instance();
   @ServicesInject(
      target = TanksServices.class
   )
   private TanksServices tanksServices = TanksServices.getInstance();
   @ServicesInject(
      target = LobbysServices.class
   )
   private LobbysServices lobbyServices = LobbysServices.getInstance();

   public RMIPaymentCallbackImpl() throws RemoteException {
      Logger.log("RMI Payment service is runned!");
   }

   public boolean paymentAccepted(long idPayment, String userId, int sum) throws RemoteException {
      Payment payment = this.database.getPaymentById(idPayment);
      if (payment == null) {
         return false;
      } else if (payment.getStatus() == 1) {
         return false;
      } else if (payment.getSum() != sum) {
         return false;
      } else {
         User user = this.database.getUserById(userId);
         if (user == null) {
            return false;
         } else {
            user.addCrystall(sum * 3);
            payment.setStatus((byte)1);
            LobbyManager userLobby = this.lobbyServices.getLobbyByNick(userId);
            if (userLobby != null) {
               this.tanksServices.dummyAddCrystall(userLobby, sum * 3);
            }

            this.database.update(user);
            this.database.update(payment);
            return true;
         }
      }
   }
}
