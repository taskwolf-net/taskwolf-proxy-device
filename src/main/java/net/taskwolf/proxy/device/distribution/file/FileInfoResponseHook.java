package net.taskwolf.proxy.device.distribution.file;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.file.event.FileInfoResponseEvent;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoResponse;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileInfoResponseHook implements Hook {
  private final FileRepository fileRepository;

  @EventHook
  private void fileInfoResponse(FileInfoResponseEvent event) {
    var client = fileRepository.findFileClient(event.infoId());
    if (client.isEmpty()) {
      return;
    }
    client.get().sendPacket(new PacketOutgoingFileInfoResponse(event.infoId(),
      event.content(), event.success()));
    fileRepository.unregisterFile(event.infoId());
  }
}
