package com.dulno.proxy.device.distribution.file.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoResponse;
import com.dulno.proxy.device.distribution.file.FileRepository;
import com.dulno.proxy.device.distribution.file.event.ProxyFileInfoResponseEvent;

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
