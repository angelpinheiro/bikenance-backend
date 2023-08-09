# Bikenance Server

This repository contains the backend server off [Bikenance](https://github.com/angelpinheiro/bikenance-backend), an
Android app that allows users to keep complete control over bike and component maintenance. This server acts as a
central hub for data
synchronization and integration with the Strava platform.

> **Bikenance is an ongoing learning project**, there may be changes or updates made at any time without prior notice.

## Features

This server provides features below to the app:

- OAuth-based authentication with Strava.
- Integration with the Strava API to allow requesting user data like the user profile and bike list.
- Implementation of the Strava WebHooks API for synchronization of new cycling activities.
- Storage and management of user profiles, cycling activities, and bike information.
- Push notifications using Firebase Cloud Messaging (FCM).

## Architecture

The server is built upon the Clean Architecture philosophy, emphasizing a pragmatic approach, which means avoiding
overengineering while integrating essential principles of clean and modular design.

#### Tech stack

- The server is build in Kotlin using Ktor.
- MongoDB is used for data storage (along with KMongo).
- The communication between the Bikenance Android app and the server is primarily done through REST services, you can
  find them on the `api` package.
- Firebase Cloud Messaging (FCM) is used to send push notifications to the app.
- Koin is used for dependency injection

### Flow of Information

The server communicates with the app and with Strava as shown below:

1. User Authentication with Strava: Users log in to the Bikenance app using OAuth with Strava. The server acts as
   a gateway for authentication, adding sensitive info to the client requests.

2. User Profile creation: After a successful login, the server queries the Strava API to retrieve user's bikes and rides
   and store them in the Bikenance database.

3. Activity Synchronization: When a user completes a cycling activity, Strava sends the activity data to the Bikenance
   server. The activity data is stored in the Bikenance server's database, and the app is notified.

## Deployment

The repository includes a Dockerfile and a docker-compose file. Before deployment, it's important
to obtain the necessary files and credentials from Strava and Firebase and customize configuration.

* To use the Strava API you need to register an application on the Strava Developers website. This registration will
  provide you with the necessary parameters to make requests: `strava.client_id` and `strava.client_secret`.
* This server also uses Firebase for push notifications, so you need to create a Firebase project and configure it. Then
  replace the existing `firebase-adminsdk.json` file with the one generated from your Firebase project.

## License

Bikenance is [licensed](LICENSE.md) under
the [CC BY-NC-SA 4.0 License](https://creativecommons.org/licenses/by-nc-sa/4.0/)