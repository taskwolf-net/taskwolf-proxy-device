package com.dulno.proxy.device.distribution.file.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileStorageRequest;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileStorageResponse;
import com.dulno.proxy.device.distribution.device.DeviceRepository;
import com.dulno.proxy.device.distribution.file.FileRepository;
import com.dulno.proxy.device.distribution.file.FileStorageRepository;
import com.dulno.proxy.device.distribution.file.event.ProxyFileStorageRequestEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileStorageRequestHook implements Hook {
  private final DeviceRepository deviceRepository;
  private final FileRepository fileRepository;
  private final FileStorageRepository fileStorageRepository;

  @EventHook
  private void fileStorageRequest(ProxyFileStorageRequestEvent event) {
    var client = deviceRepository.findDeviceClient(event.deviceId());
    if (client.isEmpty()) {
      event.client().sendPacket(new PacketOutgoingFileStorageResponse(
        event.storeId(), false));
      return;
    }
    client.get().sendPacket(new PacketOutgoingFileStorageRequest(event.storeId(),
      event.deviceId(), event.filePath(), event.fileName(), event.content()));
    fileRepository.registerFile(event.storeId(), event.client());
    fileStorageRepository.registerStorage(event.storeId(), client.get());
  }
}

