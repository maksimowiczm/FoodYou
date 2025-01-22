Open food facts search engine often goes down. Instead of blaming them for not providing FREE data
use this docker container to cache the data and make development easier.

```bash
docker compose up
```

Setup `OPEN_FOOD_FACTS_URL` in `build.gradle.kts` file to point to the docker container. Usually it
is your local IP address, something like `http://192.168.x.y:8080`. You also have to enable android
clear text traffic in android manifest file to allow http traffic.

```kotlin
build.gradle.kts

android {
    //  ...

    defaultConfig {
        // ...

        buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"<cache-address>\"")
    }
}
```

```xml
AndroidManifest.xml

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    
    <!-- ... -->

    <application 
        android:usesCleartextTraffic="true"
    >
        <activity>
            <!-- ... -->
        </activity>
    </application>

</manifest>
```