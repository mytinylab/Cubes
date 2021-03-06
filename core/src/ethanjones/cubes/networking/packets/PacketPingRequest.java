package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Direction(PacketDirection.TO_SERVER)
@Priority(PacketPriority.HIGH)
public class PacketPingRequest extends Packet {
  public long clientTime = System.nanoTime();
  
  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeLong(clientTime);
  }
  
  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    clientTime = dataInputStream.readLong();
    
    PacketPingReply ppr = new PacketPingReply();
    ppr.clientTime = clientTime;
    getSocketMonitor().getSocketOutput().getPacketQueue().add(ppr);
  }
  
  @Override
  public void handlePacket() {
    
  }
}
