package net.taskwolf.proxy.device.distribution.file.event;

import net.taskwolf.core.event.Event;
import net.taskwolf.proxy.distribution.client.ProxyClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class ProxyFileInfoRedirectRequestEvent extends Event {
  private final UUID infoId;
  private final ProxyClient client;
}
