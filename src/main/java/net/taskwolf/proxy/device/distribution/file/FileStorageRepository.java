package net.taskwolf.proxy.device.distribution.file;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.proxy.device.distribution.device.DeviceRequestEntry;
import net.taskwolf.proxy.distribution.client.ProxyClient;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileStorageRepository {
  private final Map<DeviceRequestEntry, ScheduledFuture<?>> clients = Maps.newHashMap();
  private final ScheduledExecutorService executorService =
    Executors.newSingleThreadScheduledExecutor();

  public void registerStorage(UUID storage, ProxyClient client) {
    var schedule = executorService.schedule(() -> unregisterStorage(storage),
      10, TimeUnit.SECONDS);
    clients.put(DeviceRequestEntry.create(storage, client), schedule);
  }

  public void unregisterStorage(UUID storage) {
    var clientOptional = clients.keySet().stream()
      .filter(request -> request.id().equals(storage))
      .findFirst();
    if (clientOptional.isEmpty()) {
      return;
    }
    var content = clientOptional.get();
    clients.get(content).cancel(true);
    clients.remove(content);
  }

  public Optional<ProxyClient> findStorageClient(UUID storage) {
    return clients.keySet().stream()
      .filter(request -> request.id().equals(storage))
      .map(DeviceRequestEntry::client)
      .findFirst();
  }
}
