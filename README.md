# Bikenance Server

This repository contains the backend server code for the Bikenance Android application. Bikenance is an application that allows users to keep complete control over bike and component maintenance. The server acts as a central hub for data synchronization and integration with the Strava platform.

**This is an ongoing learning project** that I build to explore and enhance my programming skills and try new stuff. There may be changes or updates made at any time without prior notice.

## Features

The Bikenance Server offers the following features:

- OAuth-based authentication with Strava for user login and access token management.
- Integration with the Strava API to allow requesting user data like the user profile and bike list.
- Storage and management of user profiles, bike data, and cycling activity information.
- Implementation of the Strava WebHooks API for synchronization of new cycling activities.
- Push notifications using Firebase Cloud Messaging (FCM).

## Architecture

The Bikenance Server is part of a client-server architecture, with the Android app serving as the client and the server handling data management and integration:

- The server is build in Kotlin using Ktor, a flexible and asynchronous web framework for building server applications.
- A MongoDB database is used to store user profiles, bike data, and activity information.
- The communication between the Bikenance Android app and the server is primarily done through RESTful services. The server exposes a set of endpoints that the app can interact with to perform various operations such as user authentication, bike and components registration, maintenance tracking, and more. In addition to RESTful services, the server utilizes Firebase Cloud Messaging (FCM) to send push notifications to the app.


### Flow of Information

The flow of information involves the app, the Bikenance server, and Strava:

1. User Authentication:
    - Users log in to the Bikenance mobile app using OAuth with Strava. The app requests authorization from the Strava API, and upon successful authentication, the app receives an access token.
    - The app sends the access token and the user's Strava athlete ID to the Bikenance server.

   For the secure storage of the "client_id" and "secret" parameters required for OAuth with Strava, this server acts as a gateway for authentication, adding sensitive info to the client requests.

2. User Profile Creation and Bike Data Retrieval:
    - The Bikenance server receives the access token and athlete ID.
    - If it's the user's first login, the server creates a user profile and stores the access token for future use.
    - The server queries the Strava API to retrieve the user's bike list and store it in the Bikenance database.

3. Activity Synchronization:
    - When a user completes a cycling activity, Strava sends the activity data to the Bikenance server.
    - The server identifies the user based on the received Strava athlete ID and associates the activity with the corresponding user in the database.
    - The activity data is stored in the Bikenance server's database.

4. Notification to the App:
    - After the activity is successfully synchronized, the Bikenance server sends a push notification to the user's mobile app using Firebase Cloud Messaging (FCM).
    - The app receives the notification and displays it to the user, indicating that the new activity has been synchronized.


## Deployment 

* To use the Strava API you need to register an application on the Strava Developers website. This registration will provide you with the necessary parameters to make requests: `strava.client_id` and `strava.client_secret`.

* This server also uses Firebase for push notifications, so you need to create a Firebase project and configure it:
  - Create a new project in the [Firebase Console](https://console.firebase.google.com), and enable Firebase Cloud Messaging (FCM) to send push notifications from the server to the Bikenance app.
  - Replace the existing `firebase-adminsdk.json` file with the one generated from your Firebase project.

Then update the `application.conf` file with appropriate URLs, database host, server port, and Strava API client credentials for your deployment.

## License

The Bikenance Server is released under the [MIT License](LICENSE.md). Please review the license file for more information.

