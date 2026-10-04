package net.taskwolf.proxy.device.distribution.device;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.taskwolf.proxy.distribution.client.ProxyClient;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class DeviceRequestEntry {
  private final UUID id;
  private final ProxyClient client;
}
