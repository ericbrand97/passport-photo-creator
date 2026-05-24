# Passport Photo Creator

An open-source Android app for creating printable biometric passport photos with visual guide overlays.

## Features

- Loading a photo from the device
- Adjusting framing via pan, zoom, and rotation
- Showing visual guides for position and face height
- Printing the passport photo in a 2x2 grid

This app is intended to help users create printed passport photos as they are required for the German Driver's License, but it is not officially recognized or certified. Always check that the resulting prints actually meet the official requirements.

## Installation

Install the APK file from GitHub releases manually or via Obtainium.

## Building from source

1. Clone the repository
2. Run `./gradlew assembleRelease` from the root of the repository

## Roadmap

- [x] Interactive crop preview
- [x] Positional and face-height guide overlays
- [x] Image picker
- [x] Printing the resulting image in a 2x2 grid
- [ ] Saving the image as a JPG for digital use
- [ ] Support for passport photos from additional countries
- [ ] Additional print layouts
- [ ] In-app camera

## License

This app is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.
