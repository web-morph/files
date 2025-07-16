# WebMorph Files

This project provides a modular file handling service for audio, image, and video files, built with Java, Spring Boot, Project Reactor, and OpenCV. It supports file upload, processing (conversion, resizing, blurring, preview generation), and event-driven extensibility.

<p align="center">
<a href="https://github.com/web-morph/files?tab=LGPL-3.0-1-ov-file"><img alt="License" src="https://img.shields.io/github/license/web-morph/files"></a>
<a href="https://docs.gradle.org/8.14/release-notes.html"><img src="https://img.shields.io/badge/Gradle-8.14-brightgreen.svg?colorB=469C00&logo=gradle"></a>
<a href="https://repo.billmarssoft.com/api/maven/latest/file/releases/com/github/webmorph/files?extension=jar" target="_blank"><img alt="Download" src="https://repo.billmarssoft.com/api/badge/latest/releases/com/github/webmorph/files"></a>
<a href="https://repo.billmarssoft.com/javadoc/releases/com/github/webmorph/files/latest" target="_blank"><img alt="Download" src="https://img.shields.io/badge/javadoc-latest-red"></a>
</p>

---

## ✅ Features
* Audio Processing: Convert uploaded audio files to AAC/MP4 using FFmpeg.
* Image Processing: Resize, blur, and convert images to WebP using OpenCV.
* Video Processing: Convert videos to H.264/AAC MP4, generate preview images, and track progress.
* Reactive Streams: All file operations are non-blocking and use Project Reactor (Mono/Flux).
* Event-Driven: Uses a custom event bus for extensibility and integration.
* Spring Boot Integration: Easily configurable and ready for use in Spring Boot applications.

## ⚙️ Requirements

* Java 17 or above

## 📦 Installation

**Note**: to view supported OS and architectures, please refer to the [javacpp-presets documentation](https://github.com/bytedeco/javacpp-presets).

⚙️ Gradle (Kotlin DSL – build.gradle.kts)

```kts
repositories {
    maven("https://repo.billmarssoft.com/public/")
}

dependencies {
    implementation("com.github.webmorph:files:<version>")
    // maven-central
    implementation("org.bytedeco:opencv:4.11.0-1.5.12:<os>-<arch>")
    implementation("org.bytedeco:openblas:0.3.30-1.5.12:<os>-<arch>")
    implementation("org.bytedeco:ffmpeg:7.1.1-1.5.12:<os>-<arch>-gpl")
}
```

⚙️ Gradle (Groovy DSL – build.gradle)

```groovy
repositories {
    maven {
        url 'https://repo.billmarssoft.com/public/'
    }
}

dependencies {
    implementation "com.github.webmorph:files:<version>"
    // maven-central
    implementation "org.bytedeco:opencv:4.11.0-1.5.12:<os>-<arch>"
    implementation "org.bytedeco:openblas:0.3.30-1.5.12:<os>-<arch>"
    implementation "org.bytedeco:ffmpeg:7.1.1-1.5.12:<os>-<arch>-gpl"
}
```

------

# 🛠️ Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.

## 🧍 Author

### [CKATEPTb](https://github.com/CKATEPTb), [fakeivchenko](https://github.com/fakeivchenko)