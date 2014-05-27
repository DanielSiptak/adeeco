ADEECo is a migration project for JDEECo to Android platform.

Where JDEECo is a Java implementation of the DEECo component system. 

##Requirements
To deploy and run ADEECo application
* Android SDK 
  - http://developer.android.com/sdk/index.html
* Android version >= HoneyComb (Android 3.0) 
  - http://developer.android.com/about/versions/android-3.0-highlights.html
* Device supporting multi-cast messaging

##Used projects
- JDEECo version before introducing Java 1.7 version functionality
- JGroups version 2.12.0.Alpha3 ported for Android
- android-support-v7-appcompat for compatibility with lower versions of Android

##Build and deploy
To build the project these steps are needed

- Download and install current version of Android SDK
- Checkout source code from [https://github.com/DanielSiptak/adeeco.git] 
- Import projects to eclipse with Android SDK and ADT support
- Android activity to run `AdeecoActivity.java` in `cz.cuni.mff.ms.dsiptak.adeecolib` package
- Create new `Android Virtual Device` with Android version higher than HoneyComb (Android 3.0)

## Project structure
There are several eclipse projects currently available.
- `AdeecoLib` Main project combining jdeeco source code with android application code.
- `android-support-v7-appcompat` Support project to enable compatibility for versions below 3.0 (Currently disabled)
- `jjdeeco` Support project for developing jGroups based knowledge repository for JDEECo.

`jjdeeco` project is standard java project and therefore it can be run without Android environment. This is easing developing of Jgroups knowledge repository.
Currently the source code has to be manually synchronized to `AdeecoLib` but this will be changed.

##Demo application
Current `AdeecoLib` application is serving as demo for starting cloud demo from JDEECo.
It is starting components and ensembles independently of each other.

##Development status
ADEECo is fully working on device locally.
JGroups implementation of Knowledge Repository is under development 
and with it possibility for multi device communication.
 

##References

* https://github.com/d3scomp/JDEECo
* http://d3s.mff.cuni.cz/projects/components_and_services/deeco/
