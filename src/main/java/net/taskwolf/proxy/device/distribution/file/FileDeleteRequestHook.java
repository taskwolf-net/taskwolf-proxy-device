package net.taskwolf.proxy.device.distribution.file;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.file.event.FileDeleteRequestEvent;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileDeleteRequest;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileDeleteResponse;
import net.taskwolf.proxy.device.distribution.device.DeviceRepository;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileDeleteRequestHook implements Hook {
  private final DeviceRepository deviceRepository;
  private final FileRepository fileRepository;

  @EventHook
  private void fileDeleteRequest(FileDeleteRequestEvent event) {
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
