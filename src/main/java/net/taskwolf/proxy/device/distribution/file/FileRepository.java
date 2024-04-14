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
public final class FileRepository {
  private final Map<UUID, DistributionClient> files = Maps.newHashMap();

  public void registerFile(UUID file, DistributionClient client) {
    files.put(file, client);
  }

  public void unregisterFile(UUID file) {
    files.remove(file);
  }

  public Optional<DistributionClient> findFileClient(UUID file) {
    return Optional.ofNullable(files.get(file));
  }
}
