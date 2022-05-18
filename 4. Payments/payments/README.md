### Инструкция по использованию библиотеки Платежи

#### Импортируйте зависимости

##### 1. Скопируйте библиотеку в формате aar в папку библиотек главного модуля
##### 2. Добавьте в build.gradle приложения следующие зависимости

```
dependencies {
        classpath 'com.android.tools.build:gradle:7.1.2'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10"
        classpath 'androidx.navigation:navigation-safe-args-gradle-plugin:2.4.1'
        classpath "com.google.dagger:hilt-android-gradle-plugin:2.38.1"
}
```

##### 3. Добавьте в build.gradle модуля зависимость для библиотеки payments-debug.aar и ее зависимости

```
plugins {
    ...
    id 'dagger.hilt.android.plugin'
}

dependencies {
    implementation(name:'payments-debug', ext:'aar')

    implementation "androidx.core:core-ktx:1.7.0"
    implementation "androidx.appcompat:appcompat:1.4.1"
    implementation "com.google.android.material:material:1.5.0"
    implementation "androidx.constraintlayout:constraintlayout:2.1.3"

    // kotlin
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0"
    implementation "androidx.fragment:fragment-ktx:1.4.1"

    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.4.1"

    // ViewModel
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1"
    // LiveData
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.4.1"

    // navigation
    implementation "androidx.navigation:navigation-fragment-ktx:2.4.1"
    implementation "androidx.navigation:navigation-ui-ktx:2.4.1"

    // hilt
    implementation "com.google.dagger:hilt-android:2.38.1"
    kapt "com.google.dagger:hilt-android-compiler:2.38.1"

    // retrofit
    implementation "com.squareup.retrofit2:retrofit:2.9.0"
    implementation "com.squareup.retrofit2:converter-gson:2.9.0"
    implementation "com.squareup.retrofit2:converter-scalars:2.9.0"

    // Coil
    implementation "io.coil-kt:coil:2.0.0-rc02"

    // pdf view
    implementation "com.github.barteksc:android-pdf-viewer:2.8.2"

    // tests
    testImplementation "junit:junit:4.13.2"
    testImplementation "androidx.test.ext:junit-ktx:1.1.3"
    testImplementation "androidx.test:core-ktx:1.4.0"
    testImplementation "org.robolectric:robolectric:4.4"

    androidTestImplementation "androidx.test.ext:junit:1.1.3"
    androidTestImplementation "androidx.test:runner:1.4.0"
    androidTestImplementation "androidx.test.espresso:espresso-core:3.4.0"
}
```

##### 4. Для работы Hilt dependency injection необходимо добавить
@HiltAndroidApp анотацию к Application классу приложения
##### 5. Для инициализации модуля Платежи необходимо вызвать
функцию initPayments из Application класса приложения

```
open class MobileApplication : Application(), ... {
    ...
    override fun onCreate() {
        super.onCreate()
        ...
        initPayments(this@MobileApplication)
    }
}
```

###### Используйте PaymentFragment как корневой фрагмент для работы с модулем Платежи
