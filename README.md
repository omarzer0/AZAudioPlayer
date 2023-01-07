# AZAudioPlayer

## Jetpack Compose audio player app featuring Exo player, getting local audio from the device showing all audio files, artists, albums, favorite playlists, customizing new playlists, searching for audio files with artists, albums, and audio file names, supporting dark and light modes and supporting both English and Arabic languages.

## Sample preview (The full preview is below)

<p>
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/1d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/5d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/1l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/5l.jpg" width="150">
</p>

## Features:
  - Get All local Audio file changes (deletions, additions, and updates).
  - Show all Audio files, artists, albums, and favorite playlists.
  - Search for Audio by artist, album, or audio name.
  - Add, rename and delete custom playlists.
  - Enable skipping android directory Audio files and recordings.
  - Sort Audio files by recent changes, artist name, or audio name.
  - Sort Albums ascending or descending.
  - Handle Audio playing when become noisy (when other audio plays) or when detaching a headphone.
  - Show notification with rewind, pause, and fast forward and show audio cover.
  - Add an Audio file to favorite, repeat all or repeat once and shuffle the order of playing audio files.
  - Support English and Arabic languages.
  - Support Dark and Light modes.
  
## Tech stack & Open-source libraries
- Minimum SDK level 21
- [Kotlin](https://kotlinlang.org/) based, [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) together with [Flow](https://developer.android.com/kotlin/flow) for asynchronous streams and one side viewModel to fragment communication.

- Dagger Hilt for dependency injection.
- Exo Player: Provides an alternative to Android's MediaPlayer API for playing audio locally and over the Internet.
- Room: Persistence library that provides an abstraction layer over SQLite database.
- Datastore: to store key-value pairs or typed objects with protocol buffers.
- SaveStateHandler to handle process death and pass data between fragments.
- [Jetpack Compose](https://developer.android.com/jetpack/compose/documentation) for the UI.
- [Coil Compose](https://coil-kt.github.io/coil): An image loading library for Android backed by Kotlin Coroutines for loading async images for compose.
- [Holi Colors](https://github.com/PatilSiddhesh/Holi) A wide collection of colors from different palettes for easy access in Compose application.
- [Accompanist](https://github.com/google/accompanist): A group of libraries that aim to supplement Jetpack Compose with features that are commonly required by developers 
  - [Compose Pager](https://google.github.io/accompanist/pager/) Provides paging layouts for Jetpack Compose.
  - [System UI controller](https://google.github.io/accompanist/systemuicontroller/) Provides easy-to-use utilities for updating the System UI bar colors within Jetpack Compose.
  - [Extended Icons](https://developer.android.com/reference/kotlin/androidx/compose/material/icons/package-summary) Provides a full set of Material icons to be used with Compose.
  
- JetPack:
  - Lifecycle - Dispose of observing data when the lifecycle state changes.
  - Fragments - Present a reusable portion of the app's UI.
  - ViewModel - UI-related data holder, lifecycle aware.
  - Navigation Component (Not compose navigation) - Makes it easier to navigate between different screens and pass data in a type-safe way.

- Architecture:
  - [MVI Architecture (Model-View-Intent)](https://www.raywenderlich.com/817602-mvi-architecture-for-android-tutorial-getting-started).
  - Repository pattern.

# Preview
<p>
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/1d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/2d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/3d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/4d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/5d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/6d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/7d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/10d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/11d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/12d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/13d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/14d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/15d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/16d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/17d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/18l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/19d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/photo_2023-01-03_16-05-56%20(6).jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/8d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/dark/9d.jpg" width="150">
</p>

<p>
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/1l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/2l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/3l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/4l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/5l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/6l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/7l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/10l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/12l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/13l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/14l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/15l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/16l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/17l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/18d.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/19l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/8l.jpg" width="150">
<img src="https://github.com/omarzer0/ImagesAndVideos/blob/main/az%20audio%20player/light/9l.jpg" width="150">
</p>
