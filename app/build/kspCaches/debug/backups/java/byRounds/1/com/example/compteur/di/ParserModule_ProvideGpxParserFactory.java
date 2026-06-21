package com.example.compteur.di;

import com.example.compteur.data.gpx.GpxParser;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ParserModule_ProvideGpxParserFactory implements Factory<GpxParser> {
  @Override
  public GpxParser get() {
    return provideGpxParser();
  }

  public static ParserModule_ProvideGpxParserFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GpxParser provideGpxParser() {
    return Preconditions.checkNotNullFromProvides(ParserModule.INSTANCE.provideGpxParser());
  }

  private static final class InstanceHolder {
    private static final ParserModule_ProvideGpxParserFactory INSTANCE = new ParserModule_ProvideGpxParserFactory();
  }
}
