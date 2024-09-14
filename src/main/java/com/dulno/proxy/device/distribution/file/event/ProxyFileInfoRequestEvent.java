package com.dulno.proxy.device.distribution.file.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import com.dulno.core.event.Event;
import com.dulno.proxy.distribution.client.ProxyClient;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class ProxyFileInfoRequestEvent extends Event {
  private final UUID infoId;
  private final String deviceId;
  private final String filePath;
  private final String fileName;
  private final ProxyClient client;
}
