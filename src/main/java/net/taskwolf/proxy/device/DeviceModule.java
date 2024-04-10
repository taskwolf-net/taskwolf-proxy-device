package net.taskwolf.proxy.device;

import com.google.inject.Injector;
import net.taskwolf.core.distribution.packet.PacketEventRepository;
import net.taskwolf.core.distribution.packet.PacketRegistry;
import net.taskwolf.core.event.HookRegistry;
import net.taskwolf.core.log.Log;
import net.taskwolf.device.distribution.event.*;
import net.taskwolf.device.distribution.packet.incoming.*;
import net.taskwolf.proxy.device.distribution.*;
import net.taskwolf.proxy.module.ProxyModule;
import net.taskwolf.proxy.module.ProxyModuleDescription;
import net.taskwolf.proxy.module.ProxyModuleLoadPriority;

@ProxyModuleDescription(name = "device", version = "1.0.0-SNAPSHOT",
  priority = ProxyModuleLoadPriority.NEUTRAL)
public final class DeviceModule extends ProxyModule {
  private Log log;

  public DeviceModule(Injector injector) {
    super(injector.createChildInjector(DeviceInjectionModule.create()));
  }

  @Override
  public void enable() throws Exception {
    log = injector().getInstance(Log.class).subLog("Device");
    registerPackets();
    registerPacketEvents();
    registerHooks();
  }

  private void registerPackets() throws Exception {
    var packetRegistry = injector().getInstance(PacketRegistry.class);
    packetRegistry.registerPacket(PacketIncomingDeviceLogin.class);
    packetRegistry.registerPacket(PacketIncomingDeviceLogout.class);
    packetRegistry.registerPacket(PacketIncomingNotificationRequest.class);
    packetRegistry.registerPacket(PacketIncomingNotificationResponse.class);
    packetRegistry.registerPacket(PacketIncomingCommandRequest.class);
    packetRegistry.registerPacket(PacketIncomingCommandResponse.class);
  }

  private void registerPacketEvents() {
    var packetEventRepository = injector().getInstance(PacketEventRepository.class);
    packetEventRepository.registerEvent(PacketIncomingDeviceLogin.class,
      (client, packet) -> DeviceLoginEvent.create(packet.deviceId(), client));
    packetEventRepository.registerEvent(PacketIncomingDeviceLogout.class,
      (client, packet) -> DeviceLogoutEvent.create(packet.deviceId()));
    packetEventRepository.registerEvent(PacketIncomingNotificationRequest.class,
      (client, packet) -> NotificationRequestEvent.create(packet.notificationId(),
        packet.deviceId(), packet.title(), packet.body(), client));
    packetEventRepository.registerEvent(PacketIncomingNotificationResponse.class,
      (client, packet) -> NotificationResponseEvent.create(packet.notificationId(),
        packet.delivered()));
    packetEventRepository.registerEvent(PacketIncomingCommandRequest.class,
      (client, packet) -> CommandRequestEvent.create(packet.commandId(),
        packet.deviceId(), packet.command(), client));
    packetEventRepository.registerEvent(PacketIncomingCommandResponse.class,
      (client, packet) -> CommandResponseEvent.create(packet.commandId(),
        packet.delivered(), packet.output(), packet.errorMessage(),
        packet.exitCode()));
  }

  private void registerHooks() {
    var hookRegistry = injector().getInstance(HookRegistry.class);
    hookRegistry.register(injector().getInstance(DeviceLoginHook.class));
    hookRegistry.register(injector().getInstance(DeviceLogoutHook.class));
    hookRegistry.register(injector().getInstance(NotificationRequestHook.class));
    hookRegistry.register(injector().getInstance(CommandRequestHook.class));
    hookRegistry.register(injector().getInstance(CommandResponseHook.class));
  }

  @Override
  public void disable() {

  }
}