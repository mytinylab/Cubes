package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.data.Data;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.side.server.ModularWorldServer;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.BlankArea;

public class PacketRequestWorld extends Packet {

  public int areaX;
  public int areaY;
  public int areaZ;

  @Override
  public void handlePacket() {
    Area area = ModularWorldServer.instance.world.getArea(areaX, areaY, areaZ);
    if (area instanceof BlankArea) return;
    PacketArea packetArea = new PacketArea();
    packetArea.areaX = areaX;
    packetArea.areaY = areaY;
    packetArea.areaZ = areaZ;
    packetArea.area = area.write();
    getSocketMonitor().queue(packetArea);
  }

  @Override
  public Data write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setInteger("areaX", areaX);
    dataGroup.setInteger("areaY", areaY);
    dataGroup.setInteger("areaZ", areaZ);
    return dataGroup;
  }

  @Override
  public void read(Data data) {
    DataGroup dataGroup = (DataGroup) data;
    areaX = dataGroup.getInteger("areaX");
    areaY = dataGroup.getInteger("areaY");
    areaZ = dataGroup.getInteger("areaZ");
  }
}
