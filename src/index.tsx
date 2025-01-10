import { NativeModules, Platform } from "react-native";

const LINKING_ERROR =
  `The package 'react-native-tiff-converter' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: "" }) +
  "- You rebuilt the app after installing the package\n" +
  "- You are not using Expo Go\n";

// src/index.ts

const { TiffConverter } = NativeModules;

export const convertTiffToPng = async (tiffFilePath: string, fileId?:string): Promise<string[]> => {
  try {
    const pngFilePath = await TiffConverter.convertTiffToPng(tiffFilePath, fileId) as string[];
    return pngFilePath;
  } catch (error:any) {
    throw new Error("Error converting TIFF to PNG: " + error.message);
  }
};

export default { convertTiffToPng };
