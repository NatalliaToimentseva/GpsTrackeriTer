# GPS tracker iTer
GPS tracker for detecting location, drawing route on map and saving routes to local Database. Uses Firebase for authorization.<br />

![iTer gen](https://github.com/user-attachments/assets/54a3d19b-7ad6-4491-aa9c-538ea2b70842)

## Technology stack used in development:<br />
* language - Kotlin<br />
* UI - XML <br />
* Architecture - Clean Architecture, UI layer - MVVM<br />
* Navigation - Navigation Component <br />
* Dependency injection - Koin<br />
* Asynchrony - RxJava3<br />
* DataBase - Room<br />
* Pagination - Paging-runtime, Paging-rxjava3 library<br />
* ViewPager2<br />
* Map - Osmdroid-android library<br />
* Location - FusedLocationProvider />
* Andriod components: Foreground service<br />
* Permissions - Internet, Coarse-Location, Fine-Location, Foreground-Service, Post-Notification<br />
* Authorization - Firebase Auth<br />

## 1. Navigation
Navigation is represented by two graphs:<br />
Main graph:<br />
- Fragment with View Pager and two Fragments for Login and Signup;<br />
- Fragment with BottomNavigationView (nested graph);<br />
- Details fragment;<br />

![mainGraph](https://github.com/user-attachments/assets/0b527abc-dc39-4eb9-8dbf-1a2895285144)

Nested graph:<br />
- Home fragment;<br />
- Route list fragment;<br />
- Settings fragment;<br />
The routesFragment refers to the Details fragment.<br />

![nested graph](https://github.com/user-attachments/assets/74444bff-9f77-446a-93a3-d8b814cfa408)

### 1.1 Home fragment<br />

Allows you to start or stop the route, displays the elapsed time, speed, average speed and distance. The route is drawn on the map. <br />

![iTer4](https://github.com/user-attachments/assets/52a36f89-96f9-4b8b-a1d5-f6c6019b9f6a)

You can save the route once it is completed.<br />

![iTer5](https://github.com/user-attachments/assets/c304cd6b-495f-4bdc-887e-1235365df992)

### 1.2 Route list fragment<br />

Displays a list of routes, allows you to delete a route or view its details.<br />

![iTer2](https://github.com/user-attachments/assets/c5362dfb-5996-4db1-8144-9c4d36181341)

### 1.3 Details fragment<br />

Displays route information <br />

![iTer3](https://github.com/user-attachments/assets/e7fd6db8-39ec-455f-8472-5b26b224266a)

## 2. Authorization<br />

Implemented using Firebase by login and password. The user who is logged in to the application has start destination a Home Fragment.<br />

![iTer1](https://github.com/user-attachments/assets/21cddc18-eae4-4c6f-becb-ef61c15d6746)

