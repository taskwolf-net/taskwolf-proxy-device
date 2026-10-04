package net.taskwolf.proxy.device;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.taskwolf.device.DeviceConfiguration;

import java.io.FileInputStream;

@RequiredArgsConstructor(staticName = "create")
public final class DeviceInjectionModule extends AbstractModule {
  @Override
  protected void configure() {

  }

  @Provides
  @Singleton
  DeviceConfiguration provideDeviceConfiguration() throws Exception {
    return DeviceConfiguration.createAndLoad();
  }

  @Provides
  @Singleton
  GoogleCredentials provideGoogleCredentials(
    DeviceConfiguration configuration
  ) throws Exception {
    return ServiceAccountCredentials
      .fromStream(new FileInputStream(System.getProperty("user.dir") +
        "/configurations/device/" + configuration.firebaseConfigurationName()))
      .createScoped("https://www.googleapis.com/auth/firebase.messaging");
  }
}
