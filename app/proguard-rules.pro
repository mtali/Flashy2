# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep readable line numbers in release crash reports (the obfuscation mapping is written to
# build/outputs/mapping/release/mapping.txt — upload it to Play to de-obfuscate stack traces).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- kotlinx.serialization -------------------------------------------------------------
# Navigation 3 persists the back stack by serializing the @Serializable NavKeys across process
# death. R8 fullMode can strip the generated serializers / Companion accessors, which crashes
# state restore. These rules keep them intact.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$Companion *;
}
-keepclassmembers class <2>$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclasseswithmembers,allowshrinking class **$$serializer { *; }

# The serializable NavKeys are `data object` singletons; keep the project's @Serializable types.
-keep,allowobfuscation @kotlinx.serialization.Serializable class com.mtali.flashy2.** { *; }