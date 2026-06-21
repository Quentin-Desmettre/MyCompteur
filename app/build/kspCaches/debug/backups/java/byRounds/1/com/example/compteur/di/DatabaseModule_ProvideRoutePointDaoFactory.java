package com.example.compteur.di;

import com.example.compteur.data.db.AppDatabase;
import com.example.compteur.data.db.dao.RoutePointDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideRoutePointDaoFactory implements Factory<RoutePointDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideRoutePointDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public RoutePointDao get() {
    return provideRoutePointDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideRoutePointDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideRoutePointDaoFactory(databaseProvider);
  }

  public static RoutePointDao provideRoutePointDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRoutePointDao(database));
  }
}
