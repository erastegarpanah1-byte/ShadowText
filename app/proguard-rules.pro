# ProGuard rules for ShadowText
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class ai.zaro.shadowtext.**$$serializer { *; }
-keepclassmembers class ai.zaro.shadowtext.** {
    *** Companion;
}
-keepclasseswithmembers class ai.zaro.shadowtext.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class ai.zaro.shadowtext.core.format.** { *; }
-keep class ai.zaro.shadowtext.core.encoding.** { *; }
