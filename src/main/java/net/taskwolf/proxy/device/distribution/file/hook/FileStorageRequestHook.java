package net.taskwolf.proxy.device.distribution.file.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileStorageRequest;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileStorageResponse;
import net.taskwolf.proxy.device.distribution.device.DeviceRepository;
import net.taskwolf.proxy.device.distribution.file.FileRepository;
import net.taskwolf.proxy.device.distribution.file.FileStorageRepository;
import net.taskwolf.proxy.device.distribution.file.event.ProxyFileStorageRequestEvent;

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

