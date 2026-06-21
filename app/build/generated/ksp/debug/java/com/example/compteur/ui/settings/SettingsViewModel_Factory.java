package com.example.compteur.ui.settings;

import android.content.Context;
import com.example.compteur.data.repository.SettingsRepository;
import com.example.compteur.domain.repository.DeviceRepository;
import com.example.compteur.domain.repository.StravaRepository;
import com.example.compteur.service.BleManager;
import com.example.compteur.service.HeartRateZoneService;
import com.example.compteur.service.OfflineMapManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<BleManager> bleManagerProvider;

  private final Provider<DeviceRepository> deviceRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<OfflineMapManager> offlineMapManagerProvider;

  private final Provider<HeartRateZoneService> heartRateZoneServiceProvider;

  private final Provider<StravaRepository> stravaRepositoryProvider;

  private final Provider<Context> contextProvider;

  public SettingsViewModel_Factory(Provider<BleManager> bleManagerProvider,
      Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<OfflineMapManager> offlineMapManagerProvider,
      Provider<HeartRateZoneService> heartRateZoneServiceProvider,
      Provider<StravaRepository> stravaRepositoryProvider, Provider<Context> contextProvider) {
    this.bleManagerProvider = bleManagerProvider;
    this.deviceRepositoryProvider = deviceRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.offlineMapManagerProvider = offlineMapManagerProvider;
    this.heartRateZoneServiceProvider = heartRateZoneServiceProvider;
    this.stravaRepositoryProvider = stravaRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(bleManagerProvider.get(), deviceRepositoryProvider.get(), settingsRepositoryProvider.get(), offlineMapManagerProvider.get(), heartRateZoneServiceProvider.get(), stravaRepositoryProvider.get(), contextProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<BleManager> bleManagerProvider,
      Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<OfflineMapManager> offlineMapManagerProvider,
      Provider<HeartRateZoneService> heartRateZoneServiceProvider,
      Provider<StravaRepository> stravaRepositoryProvider, Provider<Context> contextProvider) {
    return new SettingsViewModel_Factory(bleManagerProvider, deviceRepositoryProvider, settingsRepositoryProvider, offlineMapManagerProvider, heartRateZoneServiceProvider, stravaRepositoryProvider, contextProvider);
  }

  public static SettingsViewModel newInstance(BleManager bleManager,
      DeviceRepository deviceRepository, SettingsRepository settingsRepository,
      OfflineMapManager offlineMapManager, HeartRateZoneService heartRateZoneService,
      StravaRepository stravaRepository, Context context) {
    return new SettingsViewModel(bleManager, deviceRepository, settingsRepository, offlineMapManager, heartRateZoneService, stravaRepository, context);
  }
}
