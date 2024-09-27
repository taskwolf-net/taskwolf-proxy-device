package com.dulno.proxy.device.distribution.file.hook;

import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.device.distribution.file.packet.outgoing.PacketOutgoingFileInfoRedirectResponse;
import com.dulno.proxy.device.distribution.file.FileRepository;
import com.dulno.proxy.device.distribution.file.event.ProxyFileInfoRedirectRequestEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileInfoRedirectRequestHook implements Hook {
  private final FileRepository fileRepository;

  private static final String REDIRECT_URL_FORMAT =
    "http://%s/v1/device/file/info/response/";

  @EventHook
  private void fileInfoRedirectRequest(ProxyFileInfoRedirectRequestEvent event) {
    var client = fileRepository.findFileClient(event.infoId());
    if (client.isEmpty()) {
      return;
    }
    event.client().sendPacket(
      new PacketOutgoingFileInfoRedirectResponse(event.infoId(),
        String.format(REDIRECT_URL_FORMAT, client.get().hostname())));
  }
}
