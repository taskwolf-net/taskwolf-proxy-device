package net.taskwolf.proxy.device.distribution.file.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoResponse;
import net.taskwolf.proxy.device.distribution.file.FileRepository;
import net.taskwolf.proxy.device.distribution.file.event.ProxyFileInfoResponseEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileInfoResponseHook implements Hook {
  private final FileRepository fileRepository;

  @EventHook
  private void fileInfoResponse(ProxyFileInfoResponseEvent event) {
    var client = fileRepository.findFileClient(event.infoId());
    if (client.isEmpty()) {
      return;
    }
    client.get().sendPacket(new PacketOutgoingFileInfoResponse(event.infoId(),
      event.success()));
    fileRepository.unregisterFile(event.infoId());
  }
}
