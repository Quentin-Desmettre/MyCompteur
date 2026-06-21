-if class com.example.compteur.data.api.GeoPointDto
-keepnames class com.example.compteur.data.api.GeoPointDto
-if class com.example.compteur.data.api.GeoPointDto
-keep class com.example.compteur.data.api.GeoPointDtoJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.example.compteur.data.api.GeoPointDto
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-if class com.example.compteur.data.api.GeoPointDto
-keepclassmembers class com.example.compteur.data.api.GeoPointDto {
    public synthetic <init>(double,double,java.lang.Float,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
