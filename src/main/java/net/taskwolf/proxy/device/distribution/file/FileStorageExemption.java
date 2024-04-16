package net.taskwolf.proxy.device.distribution.file;

import net.taskwolf.core.distribution.Node;
import net.taskwolf.core.distribution.client.DistributionClient;
import net.taskwolf.proxy.distribution.DistributionExemption;
import org.json.JSONObject;

import java.util.Optional;
import java.util.UUID;

public final class FileStorageExemption extends DistributionExemption {
  public static FileStorageExemption create(FileStorageRepository fileStorageRepository) {
    return new FileStorageExemption(fileStorageRepository);
  }

  private final FileStorageRepository fileStorageRepository;

  private FileStorageExemption(FileStorageRepository fileStorageRepository) {
    super("/device/file/storage/response/");
    this.fileStorageRepository = fileStorageRepository;
  }

  @Override
  public Optional<Node> preference(String payload) {
    var body = new JSONObject(payload);
    if (!body.has("storage")) {
      return Optional.empty();
    }
    var storeId = parseStoreId(body.getString("storage"));
    if (storeId.isEmpty()) {
      return Optional.empty();
    }
    return fileStorageRepository.findStorageClient(storeId.get())
      .map(DistributionClient::node);
  }

  private Optional<UUID> parseStoreId(String storeId) {
    try {
      return Optional.of(UUID.fromString(storeId));
    } catch (Exception exception) {
      return Optional.empty();
    }
  }
}
