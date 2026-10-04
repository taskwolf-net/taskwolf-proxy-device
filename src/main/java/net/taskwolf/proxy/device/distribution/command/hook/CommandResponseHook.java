package net.taskwolf.proxy.device.distribution.command.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.command.packet.outgoing.PacketOutgoingCommandResponse;
import net.taskwolf.proxy.device.distribution.command.CommandRepository;
import net.taskwolf.proxy.device.distribution.command.event.ProxyCommandResponseEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class CommandResponseHook implements Hook {
  private final CommandRepository commandRepository;

  @EventHook
  private void commandResponse(ProxyCommandResponseEvent event) {
    var client = commandRepository.findCommandClient(event.commandId());
    if (client.isEmpty()) {
      return;
    }
    client.get().sendPacket(new PacketOutgoingCommandResponse(event.commandId(),
      event.delivered(), event.output(), event.errorMessage(), event.exitCode()));
    commandRepository.unregisterCommand(event.commandId());
  }
}
