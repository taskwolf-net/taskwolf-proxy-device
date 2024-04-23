package net.taskwolf.proxy.device.distribution.device;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.core.event.node.NodeDisconnectEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class NodeDisconnectHook implements Hook {
  private final DeviceRepository deviceRepository;

  @EventHook
  private void nodeDisconnect(NodeDisconnectEvent event) {
    deviceRepository.unregisterDevicesOfClient(event.client());
  }
}
