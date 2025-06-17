# Keep constructors of BaseDataStoreUserPreference classes, required for dependency injection
# because it uses reflection to instantiate these classes.
-keepclassmembers class * extends com.maksimowiczm.foodyou.core.preferences.BaseDataStoreUserPreference {
    <init>(...);
}
