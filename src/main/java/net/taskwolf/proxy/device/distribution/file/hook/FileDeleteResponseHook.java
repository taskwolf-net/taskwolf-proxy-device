package net.taskwolf.proxy.device.distribution.file.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.file.packet.outgoing.PacketOutgoingFileDeleteResponse;
import net.taskwolf.proxy.device.distribution.file.FileRepository;
import net.taskwolf.proxy.device.distribution.file.event.ProxyFileDeleteResponseEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileDeleteResponseHook implements Hook {
  private final FileRepository fileRepository;

  @EventHook
  private void fileDeleteResponse(ProxyFileDeleteResponseEvent event) {
    var client = fileRepository.findFileClient(event.deleteId());
    if (client.isEmpty()) {
      return;
    }
    client.get().sendPacket(new PacketOutgoingFileDeleteResponse(
      event.deleteId(), event.success()));
    fileRepository.unregisterFile(event.deleteId());
  }
}
