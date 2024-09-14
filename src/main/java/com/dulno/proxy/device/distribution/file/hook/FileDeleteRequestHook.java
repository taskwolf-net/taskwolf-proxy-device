package com.dulno.proxy.device.distribution.file.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileDeleteRequest;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileDeleteResponse;
import com.dulno.proxy.device.distribution.device.DeviceRepository;
import com.dulno.proxy.device.distribution.file.FileRepository;
import com.dulno.proxy.device.distribution.file.event.ProxyFileDeleteRequestEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileDeleteRequestHook implements Hook {
  private final DeviceRepository deviceRepository;
  private final FileRepository fileRepository;

  @EventHook
  private void fileDeleteRequest(ProxyFileDeleteRequestEvent event) {
    var client = deviceRepository.findDeviceClient(event.deviceId());
    if (client.isEmpty()) {
      event.client().sendPacket(new PacketOutgoingFileDeleteResponse(
        event.deleteId(), false));
      return;
    }
    client.get().sendPacket(new PacketOutgoingFileDeleteRequest(event.deleteId(),
      event.deviceId(), event.filePath(), event.fileName()));
    fileRepository.registerFile(event.deleteId(), event.client());
  }
}
