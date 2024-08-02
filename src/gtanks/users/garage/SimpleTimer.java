package gtanks.users.garage;

import gtanks.main.database.impl.DatabaseManagerImpl;
import gtanks.users.garage.items.Item;

@Deprecated
public class SimpleTimer
  implements Runnable
{
  private Item item;
  private Garage garage;

  public SimpleTimer(Garage garage, Item item)
  {
    this.garage = garage;
    this.item = item;
    start1();
  }

  private void start1() {
    new Thread(this).start();
  }

  public void run() {
    try {
      Thread.sleep(10000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (System.currentTimeMillis() >= this.item.time)
    {
      this.garage.items.remove(this.item);
      this.garage.parseJSONData();
      DatabaseManagerImpl.instance().update(this.garage);
    }
  }
}