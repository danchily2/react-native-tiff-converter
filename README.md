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

