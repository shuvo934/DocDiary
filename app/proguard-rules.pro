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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Optional SSL/TLS provider classes referenced by OkHttp.
# These rules apply when the app uses Android's default TLS provider.

# Bouncy Castle
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider

# Conscrypt
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.ConscryptHostnameVerifier

# OpenJSSE
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# Gson-এর জন্য generic type info রাখা
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*

# Model class-গুলোর নাম ও field যেন না বদলায়
-keep class ttit.com.shuvo.docdiary.login.arraylists.** { *; }

# Gson TypeToken
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Bundle-এ Serializable পাঠানোর জন্য
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}