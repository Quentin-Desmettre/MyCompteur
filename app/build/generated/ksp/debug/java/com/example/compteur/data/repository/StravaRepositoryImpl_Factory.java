package com.example.compteur.data.repository;

import com.example.compteur.data.api.StravaApi;
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
public final class StravaRepositoryImpl_Factory implements Factory<StravaRepositoryImpl> {
  private final Provider<StravaApi> stravaApiProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public StravaRepositoryImpl_Factory(Provider<StravaApi> stravaApiProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.stravaApiProvider = stravaApiProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public StravaRepositoryImpl get() {
    return newInstance(stravaApiProvider.get(), settingsRepositoryProvider.get());
  }

  public static StravaRepositoryImpl_Factory create(Provider<StravaApi> stravaApiProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new StravaRepositoryImpl_Factory(stravaApiProvider, settingsRepositoryProvider);
  }

  public static StravaRepositoryImpl newInstance(StravaApi stravaApi,
      SettingsRepository settingsRepository) {
    return new StravaRepositoryImpl(stravaApi, settingsRepository);
  }
}
