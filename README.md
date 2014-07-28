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
- EventBus version 2.2.1
- android-support-v7-appcompat for compatibility with lower versions of Android


##Build and deploy
To build the project these steps are needed

- Download and install current version of Android SDK
- Checkout source code from [https://github.com/DanielSiptak/adeeco.git] 
- Import projects to eclipse with Android SDK and ADT support
- Build and run either adeeco project of knowledgeExplorer project
- Create new `Android Virtual Device` with Android version higher than HoneyComb (Android 3.0)

## Project structure
There are several eclipse projects currently available.
- `Adeeco` Main project combining jjdeeco project with android application code.
- `android-support-v7-appcompat` Support project to enable compatibility for versions below 3.0 (Currently disabled)
- `jjdeeco` Support project for developing jGroups based knowledge repository for JDEECo and servers as source for Adeeco project.
- ` KnowledgeExplorer` This is an example application showing DEECo Could demo in GUI envrimonet. It is a more advance example that Adeeco project

##Demo application
Current `Adeeco` application is serving as demo for starting cloud demo from JDEECo.
It is starting components and ensembles independently of each other.

##Development status
All necessary parts are done.


##References

* https://github.com/d3scomp/JDEECo
* http://d3s.mff.cuni.cz/projects/components_and_services/deeco/
