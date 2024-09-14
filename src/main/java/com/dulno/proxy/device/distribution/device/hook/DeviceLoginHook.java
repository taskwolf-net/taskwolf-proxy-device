package com.dulno.proxy.device.distribution.device.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.proxy.device.distribution.device.DeviceRepository;
import com.dulno.proxy.device.distribution.device.event.DeviceLoginEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class DeviceLoginHook implements Hook {
  private final DeviceRepository deviceRepository;

  @EventHook
  private void deviceLogin(DeviceLoginEvent event) {
    deviceRepository.registerDevice(event.deviceId(), event.client());
  }
}
