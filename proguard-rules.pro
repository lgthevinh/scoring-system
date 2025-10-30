# Keep critical attributes for Spring/Jackson/Gson/DAO
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod, Exceptions, Record, SourceFile, LineNumberTable

# Start conservative. For final obfuscation, remove these two lines.
# You can also override from Gradle with -PproguardShrinkOnly=false
-dontobfuscate
-dontoptimize

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

# Keep Spring stereotypes and config classes
-keep @org.springframework.stereotype.Component class * { *; }
-keep @org.springframework.stereotype.Service class * { *; }
-keep @org.springframework.stereotype.Repository class * { *; }
-keep @org.springframework.web.bind.annotation.RestController class * { *; }
-keep @org.springframework.stereotype.Controller class * { *; }
-keep @org.springframework.context.annotation.Configuration class * { *; }
-keep @org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker class * { *; }
-keep @org.springframework.messaging.simp.annotation.SubscribeMapping class * { *; }
-keep @org.springframework.messaging.handler.annotation.MessageMapping class * { *; }

# Keep your DTOs and entities (fields are needed for JSON and DAO reflection)
-keep class org.thingai.app.scoringservice.dto.** { *; }
-keep class org.thingai.app.scoringservice.entity.** { *; }

# Keep DAO annotations and annotated classes/members
-keep @interface org.thingai.base.dao.annotations.**
-keep class ** {
    @org.thingai.base.dao.annotations.DaoTable *;
    @org.thingai.base.dao.annotations.DaoColumn *;
    @org.thingai.base.dao.annotations.DaoIgnore *;
}

# Keep handlers/services wired by Spring
-keep class org.thingai.app.scoringservice.handler.** { public *; protected *; }

# Gson model support
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Optional: keep parameter names (nicer logs if you later obfuscate)
# -keepparameternames

# If you still see warnings you deem harmless, you can add:
# -ignorewarnings
# But prefer fixing/adding precise -dontwarn for the package that warns.