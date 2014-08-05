package ethanjones.modularworld.networking;

import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.networking.client.ClientNetworking;
import ethanjones.modularworld.networking.server.ServerNetworking;

public class NetworkingManager {

  public static final String NETWORK_PARAMETER_SERVER = "SERVER";
  public static int port;
  public static String NETWORK_PARAMETER = "";

  public static ClientNetworking clientNetworking;
  public static ServerNetworking serverNetworking;

  public static boolean hasAddressToConnectTo() {
    return !NETWORK_PARAMETER.isEmpty() && !NETWORK_PARAMETER.toLowerCase().equals(NETWORK_PARAMETER_SERVER.toLowerCase());
  }

  public static boolean isServerOnly() {
    return NETWORK_PARAMETER.toLowerCase().equals(NETWORK_PARAMETER_SERVER.toLowerCase());
  }

  public static void connectClient() {
    if (NETWORK_PARAMETER.isEmpty()) {
      connectClient("127.0.0.1", port);
    } else {
      connectClient(NETWORK_PARAMETER, port);
    }
  }

  private static void connectClient(String address, int port) {
    clientNetworking = new ClientNetworking(address, port);
    clientNetworking.start();
  }


  public static void startServer() {
    startServer(port);
  }

  public static void startServer(int port) {
    serverNetworking = new ServerNetworking(port);
    serverNetworking.start();
  }

  public static void readPort() {
    port = Settings.networking_port.getIntegerSetting().getValue();
  }

  public static void stop() {
    if (clientNetworking != null) clientNetworking.stop();
    if (serverNetworking != null) serverNetworking.stop();
  }
}
