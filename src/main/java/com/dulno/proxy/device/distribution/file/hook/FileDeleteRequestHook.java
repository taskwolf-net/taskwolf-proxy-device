package com.dulno.proxy.device.distribution.file.hook;

import com.dulno.device.distribution.command.packet.outgoing.PacketOutgoingCommandRequest;
import com.dulno.device.distribution.command.packet.outgoing.PacketOutgoingCommandResponse;
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
    fileRepository.registerFile(event.deleteId(), event.client());
    var client = deviceRepository.findDeviceClient(event.deviceId());
    if (client.isPresent()) {
      client.get().sendPacket(new PacketOutgoingFileDeleteRequest(event.deleteId(),
        event.deviceId(), event.devicePlatform(), event.filePath(), event.fileName()));
      return;
    }
    if (event.devicePlatform().isDesktop()) {
      event.client().sendPacket(new PacketOutgoingFileDeleteResponse(
        event.deleteId(), false));
    }
  }
}
