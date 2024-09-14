package com.dulno.proxy.device.distribution.file.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileDeleteResponse;
import com.dulno.proxy.device.distribution.file.FileRepository;
import com.dulno.proxy.device.distribution.file.event.ProxyFileDeleteResponseEvent;

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
