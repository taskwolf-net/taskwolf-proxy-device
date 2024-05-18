package net.taskwolf.proxy.device.distribution.file.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.taskwolf.core.event.Event;
import net.taskwolf.proxy.distribution.client.ProxyClient;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class ProxyFileStorageRequestEvent extends Event {
  private final UUID storeId;
  private final String deviceId;
  private final String filePath;
  private final String fileName;
  private final byte[] content;
  private final ProxyClient client;
}
