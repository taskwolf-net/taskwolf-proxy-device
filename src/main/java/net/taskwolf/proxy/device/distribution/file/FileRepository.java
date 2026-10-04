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
public final class FileRepository {
  private final Map<DeviceRequestEntry, ScheduledFuture<?>> files = Maps.newHashMap();
  private final ScheduledExecutorService executorService =
    Executors.newSingleThreadScheduledExecutor();

  public void registerFile(UUID file, ProxyClient client) {
    var schedule = executorService.schedule(() -> unregisterFile(file),
      10, TimeUnit.SECONDS);
    files.put(DeviceRequestEntry.create(file, client), schedule);
  }

  public void unregisterFile(UUID file) {
    var clientOptional = files.keySet().stream()
      .filter(request -> request.id().equals(file))
      .findFirst();
    if (clientOptional.isEmpty()) {
      return;
    }
    var content = clientOptional.get();
    files.get(content).cancel(true);
    files.remove(content);
  }

  public Optional<ProxyClient> findFileClient(UUID file) {
    return files.keySet().stream()
      .filter(request -> request.id().equals(file))
      .map(DeviceRequestEntry::client)
      .findFirst();
  }
}
