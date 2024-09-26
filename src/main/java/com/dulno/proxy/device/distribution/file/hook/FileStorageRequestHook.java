package com.dulno.proxy.device.distribution.file.hook;

import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoRequest;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoResponse;
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
    fileRepository.registerFile(event.storeId(), event.client());
    var client = deviceRepository.findDeviceClient(event.deviceId());
    if (client.isPresent()) {
      client.get().sendPacket(new PacketOutgoingFileStorageRequest(event.storeId(),
        event.deviceId(), event.devicePlatform(), event.filePath(), event.fileName(),
        event.content()));
      //TODO
      fileStorageRepository.registerStorage(event.storeId(), client.get());
      return;
    }
    if (event.devicePlatform().isDesktop()) {
      event.client().sendPacket(new PacketOutgoingFileStorageResponse(
        event.storeId(), false));
    }
  }
}

