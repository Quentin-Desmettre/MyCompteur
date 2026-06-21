-if class com.example.compteur.data.api.StravaTokenResponse
-keepnames class com.example.compteur.data.api.StravaTokenResponse
-if class com.example.compteur.data.api.StravaTokenResponse
-keep class com.example.compteur.data.api.StravaTokenResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
