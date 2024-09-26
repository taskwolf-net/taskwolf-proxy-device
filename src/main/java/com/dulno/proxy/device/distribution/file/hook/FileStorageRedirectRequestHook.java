package com.dulno.proxy.device.distribution.file.hook;

import com.dulno.proxy.device.distribution.file.FileRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileStorageRedirectResponse;
import com.dulno.proxy.device.distribution.file.event.ProxyFileStorageRedirectRequestEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileStorageRedirectRequestHook implements Hook {
  private final FileRepository fileRepository;

  private static final String REDIRECT_URL_FORMAT =
    "http://%s/v1/device/file/storage/response/";

  @EventHook
  private void fileStorageRedirectRequest(ProxyFileStorageRedirectRequestEvent event) {
    var client = fileRepository.findFileClient(event.storageId());
    if (client.isEmpty()) {
      return;
    }
    event.client().sendPacket(
      new PacketOutgoingFileStorageRedirectResponse(event.storageId(),
        String.format(REDIRECT_URL_FORMAT, client.get().hostname())));
  }
}
