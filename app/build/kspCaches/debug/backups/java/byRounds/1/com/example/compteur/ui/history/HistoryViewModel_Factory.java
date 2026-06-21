package com.example.compteur.ui.history;

import android.content.Context;
import com.example.compteur.domain.repository.SessionRepository;
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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public HistoryViewModel_Factory(Provider<Context> contextProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(contextProvider.get(), sessionRepositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(Provider<Context> contextProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new HistoryViewModel_Factory(contextProvider, sessionRepositoryProvider);
  }

  public static HistoryViewModel newInstance(Context context, SessionRepository sessionRepository) {
    return new HistoryViewModel(context, sessionRepository);
  }
}
