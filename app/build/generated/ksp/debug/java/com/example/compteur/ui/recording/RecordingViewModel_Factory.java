package com.example.compteur.ui.recording;

import android.content.Context;
import androidx.lifecycle.SavedStateHandle;
import com.example.compteur.data.repository.SettingsRepository;
import com.example.compteur.domain.usecase.GetRoutePointsUseCase;
import com.example.compteur.domain.usecase.GetRoutesUseCase;
import com.example.compteur.domain.usecase.StartSessionUseCase;
import com.example.compteur.service.BleManager;
import com.example.compteur.service.HeartRateZoneService;
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
public final class RecordingViewModel_Factory implements Factory<RecordingViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<StartSessionUseCase> startSessionUseCaseProvider;

  private final Provider<GetRoutesUseCase> getRoutesUseCaseProvider;

  private final Provider<GetRoutePointsUseCase> getRoutePointsUseCaseProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<BleManager> bleManagerProvider;

  private final Provider<HeartRateZoneService> heartRateZoneServiceProvider;

  public RecordingViewModel_Factory(Provider<Context> contextProvider,
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<StartSessionUseCase> startSessionUseCaseProvider,
      Provider<GetRoutesUseCase> getRoutesUseCaseProvider,
      Provider<GetRoutePointsUseCase> getRoutePointsUseCaseProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<BleManager> bleManagerProvider,
      Provider<HeartRateZoneService> heartRateZoneServiceProvider) {
    this.contextProvider = contextProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.startSessionUseCaseProvider = startSessionUseCaseProvider;
    this.getRoutesUseCaseProvider = getRoutesUseCaseProvider;
    this.getRoutePointsUseCaseProvider = getRoutePointsUseCaseProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.bleManagerProvider = bleManagerProvider;
    this.heartRateZoneServiceProvider = heartRateZoneServiceProvider;
  }

  @Override
  public RecordingViewModel get() {
    return newInstance(contextProvider.get(), savedStateHandleProvider.get(), startSessionUseCaseProvider.get(), getRoutesUseCaseProvider.get(), getRoutePointsUseCaseProvider.get(), settingsRepositoryProvider.get(), bleManagerProvider.get(), heartRateZoneServiceProvider.get());
  }

  public static RecordingViewModel_Factory create(Provider<Context> contextProvider,
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<StartSessionUseCase> startSessionUseCaseProvider,
      Provider<GetRoutesUseCase> getRoutesUseCaseProvider,
      Provider<GetRoutePointsUseCase> getRoutePointsUseCaseProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<BleManager> bleManagerProvider,
      Provider<HeartRateZoneService> heartRateZoneServiceProvider) {
    return new RecordingViewModel_Factory(contextProvider, savedStateHandleProvider, startSessionUseCaseProvider, getRoutesUseCaseProvider, getRoutePointsUseCaseProvider, settingsRepositoryProvider, bleManagerProvider, heartRateZoneServiceProvider);
  }

  public static RecordingViewModel newInstance(Context context, SavedStateHandle savedStateHandle,
      StartSessionUseCase startSessionUseCase, GetRoutesUseCase getRoutesUseCase,
      GetRoutePointsUseCase getRoutePointsUseCase, SettingsRepository settingsRepository,
      BleManager bleManager, HeartRateZoneService heartRateZoneService) {
    return new RecordingViewModel(context, savedStateHandle, startSessionUseCase, getRoutesUseCase, getRoutePointsUseCase, settingsRepository, bleManager, heartRateZoneService);
  }
}
