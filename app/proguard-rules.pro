# ============================================================================
# Reglas R8/ProGuard para Crystal (release con minifyEnabled true).
# Estrategia conservadora v1: se ofuscan nombres de clases y métodos y se
# elimina código muerto, PERO se preservan los nombres de los campos y los
# constructores de las clases de la app, porque Firestore, Gson y Room mapean
# por nombre de campo (y el contrato con la app Puntos exige nombres estables).
# ============================================================================

# --- Atributos necesarios para reflexión/serialización y para stacktraces ---
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes *Annotation*, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes SourceFile, LineNumberTable

# --- Clases de la app: conservar campos y constructores (no los nombres de método) ---
# Esto mantiene intacta la (de)serialización de Firestore/Gson/Room y los POJO,
# mientras R8 sigue ofuscando nombres de clase/método y quitando lo no usado.
-keepclassmembers class crystal.crystal.** {
    <fields>;
    <init>(...);
    # Getters/setters: Firestore mapea las clases Kotlin por sus accesores (getX/isX/setX);
    # si R8 los renombra falla con "No properties to serialize found on class ...".
    *** get*();
    *** is*();
    void set*(***);
}
# Enums (values()/valueOf usados al serializar)
-keepclassmembers enum crystal.crystal.** { *; }

# --- Kotlin ---
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }

# --- Parcelable (@Parcelize) ---
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# --- Firebase / Firestore ---
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
# POJO de Firestore: anotaciones de mapeo
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName *;
}

# --- Gson ---
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# TypeToken: conservar la firma genérica de las subclases anónimas (object : TypeToken<...>() {}).
# Sin esto R8 borra el argumento de tipo y falla en runtime con
# "TypeToken must be created with a type argument". Requiere también -keepattributes Signature.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# --- Room (las entidades/DAO se procesan en compilación; conservar por si acaso) ---
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.**

# --- iText7 (PDF) ---
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**
-dontwarn org.bouncycastle.**
-dontwarn org.slf4j.**

# --- ML Kit (OCR) ---
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# --- ZXing (códigos de barras) ---
-dontwarn com.google.zxing.**

# --- AndroidSVG ---
-keep class com.caverock.androidsvg.** { *; }
-dontwarn com.caverock.androidsvg.**

# --- Glide (genera sus propias reglas; refuerzo) ---
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-dontwarn com.bumptech.glide.**

# --- ExoPlayer / Lottie suelen traer sus reglas; silenciar avisos ---
-dontwarn com.google.android.exoplayer2.**
-dontwarn com.airbnb.lottie.**
