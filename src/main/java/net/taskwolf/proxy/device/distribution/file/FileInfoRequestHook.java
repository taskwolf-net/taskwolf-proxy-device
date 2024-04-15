package net.taskwolf.proxy.device.distribution.file;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.file.event.FileInfoRequestEvent;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoRequest;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoResponse;
import net.taskwolf.proxy.device.distribution.device.DeviceRepository;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileInfoRequestHook implements Hook {
  private final DeviceRepository deviceRepository;
  private final FileRepository fileRepository;

  @EventHook
  private void fileInfoRequest(FileInfoRequestEvent event) {
    var client = deviceRepository.findDeviceClient(event.deviceId());
    if (client.isEmpty()) {
      event.client().sendPacket(new PacketOutgoingFileInfoResponse(
        event.infoId(), new byte[0], false));
      return;
    }
    client.get().sendPacket(new PacketOutgoingFileInfoRequest(event.infoId(),
      event.deviceId(), event.filePath(), event.fileName()));
    fileRepository.registerFile(event.infoId(), event.client());
  }
}
