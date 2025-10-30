-injars build/libs/scoring-system.jar
-outjars build/libs/scoring-system.jar

-dontwarn
-dontoptimize
-dontpreverify

-keep public class * {
    public static void main(java.lang.String[]);
}