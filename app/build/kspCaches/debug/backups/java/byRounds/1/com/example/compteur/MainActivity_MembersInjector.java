package com.example.compteur;

import com.example.compteur.domain.repository.StravaRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<StravaRepository> stravaRepositoryProvider;

  public MainActivity_MembersInjector(Provider<StravaRepository> stravaRepositoryProvider) {
    this.stravaRepositoryProvider = stravaRepositoryProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<StravaRepository> stravaRepositoryProvider) {
    return new MainActivity_MembersInjector(stravaRepositoryProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectStravaRepository(instance, stravaRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.example.compteur.MainActivity.stravaRepository")
  public static void injectStravaRepository(MainActivity instance,
      StravaRepository stravaRepository) {
    instance.stravaRepository = stravaRepository;
  }
}
