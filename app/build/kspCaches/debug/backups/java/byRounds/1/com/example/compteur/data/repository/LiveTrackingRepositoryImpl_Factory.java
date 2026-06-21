package com.example.compteur.data.repository;

import com.example.compteur.data.api.LiveTrackingApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class LiveTrackingRepositoryImpl_Factory implements Factory<LiveTrackingRepositoryImpl> {
  private final Provider<LiveTrackingApi> apiProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public LiveTrackingRepositoryImpl_Factory(Provider<LiveTrackingApi> apiProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.apiProvider = apiProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public LiveTrackingRepositoryImpl get() {
    return newInstance(apiProvider.get(), settingsRepositoryProvider.get());
  }

  public static LiveTrackingRepositoryImpl_Factory create(Provider<LiveTrackingApi> apiProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new LiveTrackingRepositoryImpl_Factory(apiProvider, settingsRepositoryProvider);
  }

  public static LiveTrackingRepositoryImpl newInstance(LiveTrackingApi api,
      SettingsRepository settingsRepository) {
    return new LiveTrackingRepositoryImpl(api, settingsRepository);
  }
}
