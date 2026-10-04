package net.taskwolf.proxy.device.distribution.file.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoRequest;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoResponse;
import net.taskwolf.proxy.device.distribution.device.DeviceRepository;
import net.taskwolf.proxy.device.distribution.file.FileRepository;
import net.taskwolf.proxy.device.distribution.file.event.ProxyFileInfoRequestEvent;

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
        event.infoId(), false));
    }
  }
}
