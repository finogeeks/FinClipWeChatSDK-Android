import java.util.concurrent.TimeUnit

apply plugin: 'com.android.library'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'
apply plugin: 'maven'

/**
 * 以git tag的数量作为其版本号
 * @return tag的数量
 */
def getAppVersionCode() {
    def stdout = new ByteArrayOutputStream()
    exec {
        commandLine 'git', 'tag', '--list'
        standardOutput = stdout
    }
    return stdout.toString().split("\n").size()
}

/**
 * 从git tag中获取应用的版本名称
 * @return git tag的名称
 */
def getAppVersionName() {
    def stdout = new ByteArrayOutputStream()
    exec {
        commandLine 'git', 'describe', '--abbrev=0', '--tags'
        standardOutput = stdout
    }
    return stdout.toString().replaceAll("[\\t\\n\\r]", "")
}

android {
    compileSdkVersion rootProject.ext.android.compileSdkVersion
    buildToolsVersion rootProject.ext.android.buildToolsVersion
    defaultConfig {
        minSdkVersion rootProject.ext.android.minSdkVersion
        targetSdkVersion rootProject.ext.android.targetSdkVersion

        versionCode getAppVersionCode()
        versionName getAppVersionName()

        consumerProguardFiles "consumer-rules.pro"

        ndk {
            abiFilters "armeabi", "armeabi-v7a", "x86", "mips"
        }

        resValue "string", "wechat_sdk_app_id", ""
    }
    buildTypes {
        debug {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    lintOptions {
        abortOnError false
    }
    androidExtensions {
        experimental = true
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

configurations.all {
    // 变化模块的缓存过期时间
    resolutionStrategy.cacheChangingModulesFor 0, TimeUnit.SECONDS
    // 动态版本的缓存过期时间
    resolutionStrategy.cacheDynamicVersionsFor 0, TimeUnit.SECONDS
}

dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    def deps = rootProject.ext.dependencies

    // finapplet
    compileOnly deps.finapplet

    // Kotlin
    implementation deps.kotlin

    // WeChat
    implementation deps.wechat
}

uploadArchives {
    configuration = configurations.archives
    repositories {
        mavenDeployer {
            def nexus = rootProject.ext.nexus_applet
            repository(url: nexus.url) {
                authentication(userName: nexus.username, password: nexus.password)
            }
            pom.project {
                version getAppVersionName()
                artifactId "wechat"
                groupId "com.finogeeks.mop"
                packaging "aar"
                description "wechat"
            }
        }
    }
}