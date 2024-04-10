package net.taskwolf.proxy.device.distribution;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.distribution.client.DistributionClient;

import java.util.Map;
import java.util.Optional;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class DeviceRepository {
  private final Map<String, DistributionClient> clients = Maps.newHashMap();

  public void registerDevice(String device, DistributionClient client) {
    clients.put(device, client);
  }

  public void unregisterDevice(String device) {
    clients.remove(device);
  }

  public Optional<DistributionClient> findDeviceClient(String device) {
    return Optional.ofNullable(clients.get(device));
  }
}
