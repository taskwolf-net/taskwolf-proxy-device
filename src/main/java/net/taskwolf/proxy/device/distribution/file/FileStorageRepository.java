package net.taskwolf.proxy.device.distribution.file;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.distribution.client.DistributionClient;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class FileStorageRepository {
  private final Map<UUID, DistributionClient> clients = Maps.newHashMap();

  public void registerStorage(UUID storage, DistributionClient client) {
    clients.put(storage, client);
  }

  public void unregisterStorage(UUID storage) {
    clients.remove(storage);
  }

  public Optional<DistributionClient> findStorageClient(UUID storage) {
    return Optional.ofNullable(clients.get(storage));
  }
}
