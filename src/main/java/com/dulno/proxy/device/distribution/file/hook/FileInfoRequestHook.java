package com.dulno.proxy.device.distribution.file.hook;

import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileDeleteRequest;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileDeleteResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoRequest;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoResponse;
import com.dulno.proxy.device.distribution.device.DeviceRepository;
import com.dulno.proxy.device.distribution.file.FileRepository;
import com.dulno.proxy.device.distribution.file.event.ProxyFileInfoRequestEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileInfoRequestHook implements Hook {
  private final DeviceRepository deviceRepository;
  private final FileRepository fileRepository;

  @EventHook
  private void fileInfoRequest(ProxyFileInfoRequestEvent event) {
    fileRepository.registerFile(event.infoId(), event.client());
    var client = deviceRepository.findDeviceClient(event.deviceId());
    if (client.isPresent()) {
      client.get().sendPacket(new PacketOutgoingFileInfoRequest(event.infoId(),
        event.deviceId(), event.devicePlatform(), event.filePath(), event.fileName()));
      return;
    }
    if (event.devicePlatform().isDesktop()) {
      event.client().sendPacket(new PacketOutgoingFileInfoResponse(
        event.infoId(), new byte[0], false));
    }
  }
}
