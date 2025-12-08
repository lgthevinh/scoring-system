# Keep critical attributes for Spring/Jackson/Gson/DAO
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod, Exceptions, Record, SourceFile, LineNumberTable

# Start conservative. For final obfuscation, remove these two lines.
# You can also override from Gradle with -PproguardShrinkOnly=false
# -dontobfuscate
# -dontoptimize
-repackageclasses 'org.thingai.app.internal'

# We only process app classes; libraries are on libraryjars.
# Silence library-only missing-classes warnings that are safe to ignore.
-dontwarn org.springframework.**
-dontwarn org.springframework.boot.**
-dontwarn org.springframework.expression.**
-dontwarn org.yaml.snakeyaml.**
-dontwarn ch.qos.logback.**
-dontwarn org.slf4j.**
-dontwarn org.apache.**          # Tomcat/commons in Boot may reference optional classes
-dontwarn jakarta.**
-dontwarn javax.**
-dontwarn io.micrometer.**       # Optional Hikari metrics bridge
-dontwarn com.zaxxer.hikari.metrics.**
-dontwarn com.zaxxer.hikari.util.**
-dontwarn com.sun.**
-dontwarn jdk.**

# Keep your Spring Boot main entry points
-keep class org.thingai.app.** {
    public static void main(...);
}

# Optional: keep parameter names (nicer logs if you later obfuscate)
# -keepparameternames

# If you still see warnings you deem harmless, you can add:
# -ignorewarnings
# But prefer fixing/adding precise -dontwarn for the package that warns.