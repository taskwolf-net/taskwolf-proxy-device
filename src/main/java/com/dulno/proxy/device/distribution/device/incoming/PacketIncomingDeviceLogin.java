package com.dulno.proxy.device.distribution.device.incoming;


import lombok.Getter;
import lombok.experimental.Accessors;
import com.dulno.core.packet.PacketBuffer;
import com.dulno.core.worker.packet.incoming.PacketIncoming;

@Getter
@Accessors(fluent = true)
public final class PacketIncomingDeviceLogin extends PacketIncoming {
  private String deviceId;

  public PacketIncomingDeviceLogin() {
    super(0x20);
  }

  @Override
  public void read(PacketBuffer buffer) throws Exception {
    deviceId = buffer.readString();
  }
}
