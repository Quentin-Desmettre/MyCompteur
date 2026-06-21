package com.example.compteur.di;

import com.example.compteur.data.db.AppDatabase;
import com.example.compteur.data.db.dao.GpsPointDao;
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
public final class DatabaseModule_ProvideGpsPointDaoFactory implements Factory<GpsPointDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideGpsPointDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public GpsPointDao get() {
    return provideGpsPointDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideGpsPointDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideGpsPointDaoFactory(databaseProvider);
  }

  public static GpsPointDao provideGpsPointDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideGpsPointDao(database));
  }
}
