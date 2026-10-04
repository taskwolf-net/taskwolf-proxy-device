package net.taskwolf.proxy.device.distribution.command.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.command.packet.outgoing.PacketOutgoingCommandRequest;
import net.taskwolf.device.distribution.command.packet.outgoing.PacketOutgoingCommandResponse;
import net.taskwolf.proxy.device.distribution.command.CommandRepository;
import net.taskwolf.proxy.device.distribution.command.event.ProxyCommandRequestEvent;
import net.taskwolf.proxy.device.distribution.device.DeviceRepository;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class CommandRequestHook implements Hook {
  private final DeviceRepository deviceRepository;
  private final CommandRepository commandRepository;

  @EventHook
  private void commandRequest(ProxyCommandRequestEvent event) {
    commandRepository.registerCommand(event.commandId(), event.client());
    var client = deviceRepository.findDeviceClient(event.deviceId());
    if (client.isPresent()) {
      client.get().sendPacket(new PacketOutgoingCommandRequest(event.commandId(),
        event.deviceId(), event.devicePlatform(), event.command()));
      return;
    }
    if (event.devicePlatform().isDesktop()) {
      event.client().sendPacket(new PacketOutgoingCommandResponse(
        event.commandId(), false, "", "", -1));
    }
  }
}
