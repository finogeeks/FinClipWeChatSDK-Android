#!/bin/bash

#1.环境准备
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.275.b01-0.el7_9.x86_64
export GEM_HOME=/root/.rvm
export GRADLE_HOME=/opt/gradle/gradle-5.4.1
export ANDROID_HOME=/opt/sdk
export PATH=$NDK_HOME:$GEM_HOME/bin:$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$GRADLE_HOME/bin:$JAVA_HOME/bin:$PATH


cp -r wechat/build.gradle.dev.tpl wechat/build.gradle

sed -i "s/__finapplet_version__/${version}/g" wechat/build.gradle

if [ "$upload_to_applet_nexus" = "true" ]; then
	sed -i "s/nexus_finogeeks/nexus_applet/g" wechat/build.gradle
fi

cat wechat/build.gradle

gradle clean uploadArchives

git reset --hard

