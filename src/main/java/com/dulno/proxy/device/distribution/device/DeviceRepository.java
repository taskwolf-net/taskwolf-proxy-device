package com.dulno.proxy.device.distribution.device;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.proxy.distribution.client.ProxyClient;

import java.util.Map;
import java.util.Optional;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class DeviceRepository {
  private final Map<String, ProxyClient> clients = Maps.newHashMap();

  public void registerDevice(String device, ProxyClient client) {
    clients.put(device, client);
  }

  public void unregisterDevice(String device) {
    clients.remove(device);
  }

  public void unregisterDevicesOfClient(ProxyClient client) {
    for (var entry : clients.entrySet()) {
      if (entry.getValue() == client) {
        clients.remove(entry.getKey());
      }
    }
  }

  public Optional<ProxyClient> findDeviceClient(String device) {
    return Optional.ofNullable(clients.get(device));
  }
}
