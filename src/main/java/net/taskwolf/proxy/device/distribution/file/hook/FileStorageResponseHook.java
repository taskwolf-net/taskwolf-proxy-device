package net.taskwolf.proxy.device.distribution.file.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileStorageResponse;
import net.taskwolf.proxy.device.distribution.file.FileRepository;
import net.taskwolf.proxy.device.distribution.file.event.ProxyFileStorageResponseEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileStorageResponseHook implements Hook {
  private final FileRepository fileRepository;

  @EventHook
  private void fileStorageResponse(ProxyFileStorageResponseEvent event) {
    var client = fileRepository.findFileClient(event.storageId());
    if (client.isEmpty()) {
      return;
    }
    client.get().sendPacket(new PacketOutgoingFileStorageResponse(
      event.storageId(), event.success()));
    fileRepository.unregisterFile(event.storageId());
  }
}
