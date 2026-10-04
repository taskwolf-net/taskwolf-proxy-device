package net.taskwolf.proxy.device.distribution.device.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.proxy.device.distribution.device.DeviceRepository;
import net.taskwolf.proxy.device.distribution.device.event.DeviceLogoutEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class DeviceLogoutHook implements Hook {
  private final DeviceRepository deviceRepository;

  @EventHook
  private void deviceLogout(DeviceLogoutEvent event) {
    deviceRepository.unregisterDevice(event.deviceId());
  }
}
