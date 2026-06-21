package com.example.compteur.ui.dashboard;

import com.example.compteur.data.repository.SettingsRepository;
import com.example.compteur.domain.usecase.DeleteRouteUseCase;
import com.example.compteur.domain.usecase.GetRoutePointsUseCase;
import com.example.compteur.domain.usecase.GetRoutesUseCase;
import com.example.compteur.domain.usecase.ImportGpxUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<GetRoutesUseCase> getRoutesUseCaseProvider;

  private final Provider<ImportGpxUseCase> importGpxUseCaseProvider;

  private final Provider<DeleteRouteUseCase> deleteRouteUseCaseProvider;

  private final Provider<GetRoutePointsUseCase> getRoutePointsUseCaseProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public DashboardViewModel_Factory(Provider<GetRoutesUseCase> getRoutesUseCaseProvider,
      Provider<ImportGpxUseCase> importGpxUseCaseProvider,
      Provider<DeleteRouteUseCase> deleteRouteUseCaseProvider,
      Provider<GetRoutePointsUseCase> getRoutePointsUseCaseProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.getRoutesUseCaseProvider = getRoutesUseCaseProvider;
    this.importGpxUseCaseProvider = importGpxUseCaseProvider;
    this.deleteRouteUseCaseProvider = deleteRouteUseCaseProvider;
    this.getRoutePointsUseCaseProvider = getRoutePointsUseCaseProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(getRoutesUseCaseProvider.get(), importGpxUseCaseProvider.get(), deleteRouteUseCaseProvider.get(), getRoutePointsUseCaseProvider.get(), settingsRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<GetRoutesUseCase> getRoutesUseCaseProvider,
      Provider<ImportGpxUseCase> importGpxUseCaseProvider,
      Provider<DeleteRouteUseCase> deleteRouteUseCaseProvider,
      Provider<GetRoutePointsUseCase> getRoutePointsUseCaseProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new DashboardViewModel_Factory(getRoutesUseCaseProvider, importGpxUseCaseProvider, deleteRouteUseCaseProvider, getRoutePointsUseCaseProvider, settingsRepositoryProvider);
  }

  public static DashboardViewModel newInstance(GetRoutesUseCase getRoutesUseCase,
      ImportGpxUseCase importGpxUseCase, DeleteRouteUseCase deleteRouteUseCase,
      GetRoutePointsUseCase getRoutePointsUseCase, SettingsRepository settingsRepository) {
    return new DashboardViewModel(getRoutesUseCase, importGpxUseCase, deleteRouteUseCase, getRoutePointsUseCase, settingsRepository);
  }
}
