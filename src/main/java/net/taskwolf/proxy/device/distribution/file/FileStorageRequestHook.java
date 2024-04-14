package net.taskwolf.proxy.device.distribution.file;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.file.event.FileStorageRequestEvent;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileStorageRequest;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileStorageResponse;
import net.taskwolf.proxy.device.distribution.device.DeviceRepository;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileStorageRequestHook implements Hook {
  private final DeviceRepository deviceRepository;
  private final FileRepository commandRepository;

  @EventHook
  private void fileStorageRequest(FileStorageRequestEvent event) {
    var client = deviceRepository.findDeviceClient(event.deviceId());
    if (client.isEmpty()) {
      event.client().sendPacket(new PacketOutgoingFileStorageResponse(
        event.storeId(), false));
      return;
    }
    client.get().sendPacket(new PacketOutgoingFileStorageRequest(event.storeId(),
      event.deviceId(), event.path()));
    commandRepository.registerFile(event.storeId(), event.client());
  }
}

