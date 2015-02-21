package ethanjones.cubes.common;

import ethanjones.cubes.common.block.BlockManager;
import ethanjones.cubes.common.event.EventBus;
import ethanjones.cubes.common.timing.Timing;
import ethanjones.cubes.common.networking.Networking;
import ethanjones.cubes.common.networking.NetworkingManager;

public class Sided {

  private static class Data {

    EventBus eventBus;
    Timing timing;
    BlockManager blockManager;
  }

  private static Data clientData;
  private static Data serverData;
  private static ThreadLocal<Side> sideLocal = new ThreadLocal<Side>();
  private static ThreadLocal<Boolean> mainLocal = new ThreadLocal<Boolean>() {
    @Override
    public Boolean initialValue() {
      return false;
    }
  };

  public static EventBus getEventBus() {
    return getData().eventBus;
  }

  private static Data getData() {
    Side side = getSide();
    if (side == null) {
      throw new CubesException("Sided objects cannot be accessed from thread: " + Thread.currentThread().getName());
    }
    Data data = getData(side);
    if (data == null) throw new CubesException("Sided objects have not been setup yet");
    return data;
  }

  public static Side getSide() {
    return sideLocal.get();
  }

  /**
   * Allow network threads etc. to access sided objects
   */
  public static void setSide(Side side) {
    if (mainLocal.get()) return;
    sideLocal.set(side);
  }

  private static Data getData(Side side) {
    switch (side) {
      case Client:
        return clientData;
      case Server:
        return serverData;
    }
    return null;
  }

  public static Timing getTiming() {
    return getData().timing;
  }

  public static BlockManager getBlockManager() {
    return getData().blockManager;
  }

  public static Networking getNetworking() {
    return NetworkingManager.getNetworking(getSide());
  }

  public static void setup(Side side) {
    if (side == null || getData(side) != null) return;

    sideLocal.set(side);
    mainLocal.set(true);

    Data data = new Data();
    switch (side) {
      case Client:
        clientData = data;
        break;
      case Server:
        serverData = data;
        break;
    }

    data.eventBus = new EventBus();
    data.timing = new Timing();
    data.blockManager = new BlockManager();
    if (side == Side.Server) data.blockManager.generateDefault(); //TODO Need to store with world
  }

  public static boolean isSetup(Side side) {
    return side != null && getData(side) != null;
  }

  public static void reset(Side side) {
    if (side == null) return;

    switch (side) {
      case Client:
        clientData = null;
        break;
      case Server:
        serverData = null;
        break;
    }
  }
}