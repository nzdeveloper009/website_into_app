# WebView Android Jetpack Compose Example
This is an Android app that demonstrates how to use a WebView with Jetpack Compose and Convert Website into Android App using Jetpack Compose

## Prerequisites
- Android Studio Arctic Fox (2020.3.1) or higher
- Android SDK 31 or higher

## Installation
1. Clone the repository using
```bash
git clone https://github.com/nzdeveloper009/website_into_app.git
```
2. Open the project in Android Studio.
3. Build and run the app.

## Usage
The app loads a webpage in a **WebView** and demonstrates how to configure the **WebView** settings and handle the **WebView lifecycle**.

## Code Overview
The main activity **MainActivity.kt** extends **ComponentActivity** and contains a **setContent** block that sets the content view to **MyApp**, which is a Composable function that wraps the **MainScreen** Composable function. The **setStatusBarColor** function sets the color of the status bar.

**MyApp** is a Composable function that sets the app theme and wraps the **MainScreen** Composable function.

**MainScreen** is a Composable function that creates a **WebView**, loads a URL, handles the **BackHandler**, and manages the **WebView lifecycle**.

**createWebView** is a helper function that creates a **WebView** and configures its settings.

**WebViewState** is a class that manages the **WebView lifecycle**.

**CustomWebViewClient** is a class that overrides the shouldOverrideUrlLoading function to handle WhatsApp links.

**shouldOverrideUrlLoading** function to handle WhatsApp links.
