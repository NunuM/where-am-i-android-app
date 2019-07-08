## WhereAmI Android App

### Motivation

This is a free, collaborative platform with the goal of helping researchers to test and develop indoor tracking algorithms using Wi-Fi signal information. Apart of the Android application, we also build an [Server application](https://github.com/NunuM/where-am-i-server) to help users to collect raw data more easily.

### Table Of Contents
* [Get Start](#get-started)
* [Data Model & Lifecycle](#data-model--lifecycle)
* [Android Application](#android-application)
* [REST API Docs](https://whereami.nunum.me/swagger/)
* [FAQ](#faq)

### Get Started


### Data Model & Lifecycle

This is a brief summary of the entities. Let's begin with **Localization**. This entity represents a point on the map, like your home in a satellite view. Within your home, you have various rooms, there are **Positions**. With this, we have the relationship of **one localization** can have **zero or more positions**. The localizations can be private for the device that created it. In other words, you can share with other users your localizations. The privacy is declared at its creation.


A **Training** entity is created with the desired algorithm and the respective provider. An **Algorithm** can be proposed by everyone. This is because several people like more reading than implementing and vice versa, thus **the same algorithm** can be provided by several providers and their results can be compared and competition ("I have a better random algorithm 😅")  may also be resulted by this design.

An **AlgorithmProvider** can be, for now, either a user with their own server or a user that has a git repository ready to be deployed. Both must apply to be a provider. This is just e email verification and there are ready to go.

The provider with their own server will receive all fingerprints for the **Training requests**  via HTTP. Remember that a Training request belongs to a localization that has multiples positions and each position have hundreds of Wi-Fi fingerprints. When you train the model, you must update the respective **Training** to inform that you are ready to predict the user localization. After that, your server will be queried. From this description, the server owner registers as to be an algorithm provider by giving two required HTTP resources, one for data ingestion, and another for the prediction phase. For the git owners, we integrate the repository with the platform.


When exists a training request for a given provider, the subsequent training request, will reactivate the sinking of the data to the HTTP provider, the cursor is preserved. In other words, your server will receive only newer data. 


The visible **Localizations** and **Positions** can be reported as **Spam** by sanity. And only the localization owners can submit training requests.


To conclude this chapter, the use case that this platform offers is the integration of two actors, a user who shares Wi-Fi information and a Provider who predicts the user localization using their algorithm implementation. 

### Android Application

#### Table Of Contents
* [Features](#features)
* [Permissions](#permissions)
* [FAQ Android](#faq-android)
* [ScreenShots](#screen-shots)

##### Features
* Posts view with related material about the state of art;
* Localizations management;
* Positions management;
* Customizable privacy of localizations.  
* Notification center;
* Offline localizations and posts visualization;
* Offline fingerprinting collection;
* Automatic synchronization of locally stored data;
* Highly configurable REST API;
* All lists views use endless scrolling and swipe to refresh gesture;
* No collect or sell information that is shared by the users;
* Open source;
* No Ads;

##### Permissions

* [Internet](https://developer.android.com/reference/android/Manifest.permission.html#INTERNET) - To communicate with server;
* [Access WI-FI State](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_WIFI_STATE) - For collect WI-FI samples
* [Change WI-FI State](https://developer.android.com/reference/android/Manifest.permission#CHANGE_WIFI_STATE) - For Turning On the Wi-Fi card;
* [Access Coarse Location](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_COARSE_LOCATION) - For localization associated data at their creation;

##### FAQ Android

###### There are a ton of localizations, can I only see mine?

Yes, you can. Click on top right button **&#8942;** -> **settings** -> **HTTP API** and activate the option **List only my localizations**.

###### Why the screen of my Android device is always ON when I am sending samples or requesting to predict my localization?

When the screen goes dark (by inactivity), the WI-FI card no longer scans the available networks. This was the behavior on my device, I also have tried to build the sink service as a background job, but without success. If you know a better way, let me know, or open an issue.

###### Do I need an Internet connection to see my localizations?

No, the localization will be displayed from the application cache directory. If you clean it, you will need Internet.

###### Do I need an Internet connection when am I collecting samples?

No, the application will store locally and automatically sync with server when you are back online.

###### When I delete one localization, what data is deleted?

The server will hard delete all data associated with the localization. There is no coming back;

###### You pretend to implement more indoor tracking algorithms, besides the Mean?

Yes, when the community starts to share information (currently is a cold start), I will provide more implementations.

###### Can I suggest posts and/or algorithms?

Yes, you can use the feedback form to propose new material.

###### I have more than one device, can I share my localizations to another specific device? 

Kind of, currently either you mark the localization as public or private for a list and/or predict, and is for all users. However, you can go to the **settings** -> **general** and click on **Installation ID** and set on all devices the same installation, and all will see the same localizations.

##### Screen Shots

<img src="https://i.ibb.co/n3GY6Kf/65846243-359964648034468-7578109312891879424-n.jpg" alt="Posts Fragment" width="300"/> <img src="https://i.ibb.co/gSGs1td/66173223-678601435899829-4638181899309678592-n.jpg" alt="New Localization Fragment" width="300"/> <img src="https://i.ibb.co/18p0QsF/66383260-378099049729668-3632568727754506240-n.jpg" alt="Localizations Fragment" width="300"/> 
<img src="https://i.ibb.co/HFgkTZX/66438572-348106275883337-2898324556466880512-n.jpg" alt="Localizations Dashboard Fragment" width="300"/> <img src="https://i.ibb.co/QpSkLsZ/66269079-2181320211980385-4407071160810864640-n.jpg" alt="New Training Fragment" width="300"/> <img src="https://i.ibb.co/VHHQQ5K/66240579-638161973344689-8484108607871254528-n.jpg" alt="Notification Fragment" width="300"/>
<img src="https://i.ibb.co/ftSGHRk/66526400-1527989434003740-864574528682983424-n.jpg" alt="Notification Fragment" width="300"/>

