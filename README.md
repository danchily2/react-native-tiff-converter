This is a new [**React Native**](https://reactnative.dev) project, bootstrapped using [`@react-native-community/cli`](https://github.com/react-native-community/cli).

# Getting Started

>**Note**: Make sure you have completed the [React Native - Environment Setup](https://reactnative.dev/docs/environment-setup) instructions till "Creating a new application" step, before proceeding.

## Install package


```bash
# using npm
npm i react-native-tiff-converter

# OR using Yarn
yarn add react-native-tiff-converter
```

## Use convert function

```bash
import { convertTiffToPng } from 'react-native-tiff-converter';
const pngFilePaths = await convertTiffToPng(filePath, 'fileId'); 
// pngFilePaths - array with uri to each page
// fileId - optional, needed in case you do not want to overwrite previous files
```

## 16KB Page Size Compatibility

This package includes native libraries built with 16KB page size alignment (required for Android 15+).

**Native libraries are pre-built with:**
- NDK r28 with 16KB alignment flags (`-Wl,-z,max-page-size=16384`)
- All `.so` files have proper 16KB alignment baked in at compile time
- Built from [Android-TiffBitmapFactory](https://github.com/Beyka/Android-TiffBitmapFactory) source

**Your app should use:**
- Android Gradle Plugin 8.5.1 or higher
- NDK r28 or newer (optional, only if building native code)

The native libraries work out-of-the-box with no additional configuration required.

