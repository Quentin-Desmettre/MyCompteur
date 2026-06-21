package com.example.compteur.domain.usecase;

import com.example.compteur.domain.repository.RouteRepository;
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
public final class GetRoutePointsUseCase_Factory implements Factory<GetRoutePointsUseCase> {
  private final Provider<RouteRepository> repositoryProvider;

  public GetRoutePointsUseCase_Factory(Provider<RouteRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetRoutePointsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetRoutePointsUseCase_Factory create(Provider<RouteRepository> repositoryProvider) {
    return new GetRoutePointsUseCase_Factory(repositoryProvider);
  }

  public static GetRoutePointsUseCase newInstance(RouteRepository repository) {
    return new GetRoutePointsUseCase(repository);
  }
}
