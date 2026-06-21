package com.example.compteur.ui.route_detail;

import androidx.lifecycle.SavedStateHandle;
import com.example.compteur.domain.usecase.GetRoutePointsUseCase;
import com.example.compteur.domain.usecase.GetRouteUseCase;
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
public final class RouteDetailViewModel_Factory implements Factory<RouteDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<GetRouteUseCase> getRouteUseCaseProvider;

  private final Provider<GetRoutePointsUseCase> getRoutePointsUseCaseProvider;

  public RouteDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<GetRouteUseCase> getRouteUseCaseProvider,
      Provider<GetRoutePointsUseCase> getRoutePointsUseCaseProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.getRouteUseCaseProvider = getRouteUseCaseProvider;
    this.getRoutePointsUseCaseProvider = getRoutePointsUseCaseProvider;
  }

  @Override
  public RouteDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), getRouteUseCaseProvider.get(), getRoutePointsUseCaseProvider.get());
  }

  public static RouteDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<GetRouteUseCase> getRouteUseCaseProvider,
      Provider<GetRoutePointsUseCase> getRoutePointsUseCaseProvider) {
    return new RouteDetailViewModel_Factory(savedStateHandleProvider, getRouteUseCaseProvider, getRoutePointsUseCaseProvider);
  }

  public static RouteDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      GetRouteUseCase getRouteUseCase, GetRoutePointsUseCase getRoutePointsUseCase) {
    return new RouteDetailViewModel(savedStateHandle, getRouteUseCase, getRoutePointsUseCase);
  }
}
