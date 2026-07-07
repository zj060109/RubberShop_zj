# Add project specific ProGuard rules here.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.rubbershop.app.data.model.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
