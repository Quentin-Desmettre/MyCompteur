package com.example.compteur.service;

import com.example.compteur.data.db.dao.GpsPointDao;
import com.example.compteur.data.db.dao.SensorDataDao;
import com.example.compteur.data.repository.SettingsRepository;
import com.example.compteur.domain.repository.DeviceRepository;
import com.example.compteur.domain.repository.LiveTrackingRepository;
import com.example.compteur.domain.repository.RouteRepository;
import com.example.compteur.domain.repository.SessionRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

@QualifierMetadata
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
public final class RecordingService_MembersInjector implements MembersInjector<RecordingService> {
  private final Provider<GpsPointDao> gpsDaoProvider;

  private final Provider<SensorDataDao> sensorDataDaoProvider;

  private final Provider<BleManager> bleManagerProvider;

  private final Provider<DeviceRepository> deviceRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<RouteRepository> routeRepositoryProvider;

  private final Provider<LiveTrackingRepository> liveTrackingRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<CoroutineScope> appScopeProvider;

  public RecordingService_MembersInjector(Provider<GpsPointDao> gpsDaoProvider,
      Provider<SensorDataDao> sensorDataDaoProvider, Provider<BleManager> bleManagerProvider,
      Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<RouteRepository> routeRepositoryProvider,
      Provider<LiveTrackingRepository> liveTrackingRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<CoroutineScope> appScopeProvider) {
    this.gpsDaoProvider = gpsDaoProvider;
    this.sensorDataDaoProvider = sensorDataDaoProvider;
    this.bleManagerProvider = bleManagerProvider;
    this.deviceRepositoryProvider = deviceRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.routeRepositoryProvider = routeRepositoryProvider;
    this.liveTrackingRepositoryProvider = liveTrackingRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.appScopeProvider = appScopeProvider;
  }

  public static MembersInjector<RecordingService> create(Provider<GpsPointDao> gpsDaoProvider,
      Provider<SensorDataDao> sensorDataDaoProvider, Provider<BleManager> bleManagerProvider,
      Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<RouteRepository> routeRepositoryProvider,
      Provider<LiveTrackingRepository> liveTrackingRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<CoroutineScope> appScopeProvider) {
    return new RecordingService_MembersInjector(gpsDaoProvider, sensorDataDaoProvider, bleManagerProvider, deviceRepositoryProvider, sessionRepositoryProvider, routeRepositoryProvider, liveTrackingRepositoryProvider, settingsRepositoryProvider, appScopeProvider);
  }

  @Override
  public void injectMembers(RecordingService instance) {
    injectGpsDao(instance, gpsDaoProvider.get());
    injectSensorDataDao(instance, sensorDataDaoProvider.get());
    injectBleManager(instance, bleManagerProvider.get());
    injectDeviceRepository(instance, deviceRepositoryProvider.get());
    injectSessionRepository(instance, sessionRepositoryProvider.get());
    injectRouteRepository(instance, routeRepositoryProvider.get());
    injectLiveTrackingRepository(instance, liveTrackingRepositoryProvider.get());
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
    injectAppScope(instance, appScopeProvider.get());
  }

  @InjectedFieldSignature("com.example.compteur.service.RecordingService.gpsDao")
  public static void injectGpsDao(RecordingService instance, GpsPointDao gpsDao) {
    instance.gpsDao = gpsDao;
  }

  @InjectedFieldSignature("com.example.compteur.service.RecordingService.sensorDataDao")
  public static void injectSensorDataDao(RecordingService instance, SensorDataDao sensorDataDao) {
    instance.sensorDataDao = sensorDataDao;
  }

  @InjectedFieldSignature("com.example.compteur.service.RecordingService.bleManager")
  public static void injectBleManager(RecordingService instance, BleManager bleManager) {
    instance.bleManager = bleManager;
  }

  @InjectedFieldSignature("com.example.compteur.service.RecordingService.deviceRepository")
  public static void injectDeviceRepository(RecordingService instance,
      DeviceRepository deviceRepository) {
    instance.deviceRepository = deviceRepository;
  }

  @InjectedFieldSignature("com.example.compteur.service.RecordingService.sessionRepository")
  public static void injectSessionRepository(RecordingService instance,
      SessionRepository sessionRepository) {
    instance.sessionRepository = sessionRepository;
  }

  @InjectedFieldSignature("com.example.compteur.service.RecordingService.routeRepository")
  public static void injectRouteRepository(RecordingService instance,
      RouteRepository routeRepository) {
    instance.routeRepository = routeRepository;
  }

  @InjectedFieldSignature("com.example.compteur.service.RecordingService.liveTrackingRepository")
  public static void injectLiveTrackingRepository(RecordingService instance,
      LiveTrackingRepository liveTrackingRepository) {
    instance.liveTrackingRepository = liveTrackingRepository;
  }

  @InjectedFieldSignature("com.example.compteur.service.RecordingService.settingsRepository")
  public static void injectSettingsRepository(RecordingService instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }

  @InjectedFieldSignature("com.example.compteur.service.RecordingService.appScope")
  public static void injectAppScope(RecordingService instance, CoroutineScope appScope) {
    instance.appScope = appScope;
  }
}
