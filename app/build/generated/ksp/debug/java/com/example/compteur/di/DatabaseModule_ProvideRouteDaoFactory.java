package com.example.compteur.di;

import com.example.compteur.data.db.AppDatabase;
import com.example.compteur.data.db.dao.RouteDao;
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
public final class DatabaseModule_ProvideRouteDaoFactory implements Factory<RouteDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideRouteDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public RouteDao get() {
    return provideRouteDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideRouteDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideRouteDaoFactory(databaseProvider);
  }

  public static RouteDao provideRouteDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRouteDao(database));
  }
}
