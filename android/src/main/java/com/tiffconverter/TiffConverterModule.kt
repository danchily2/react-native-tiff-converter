package com.tiffconverter

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableNativeArray
import org.beyka.tiffbitmapfactory.TiffBitmapFactory
import java.io.File
import java.io.FileOutputStream
import android.util.Log

class TiffConverterModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    companion object {
        private const val TAG = "TiffConverterModule"
    }
    override fun getName(): String {
        return "TiffConverter"
    }

    @ReactMethod
    fun convertTiffToPng(tiffFilePath: String, uuid: String, promise: Promise) {
        try {
            val tiffFile = File(tiffFilePath)

            if (!tiffFile.exists()) {
                Log.e(TAG, "TIFF file not found at $tiffFilePath")
                promise.reject("FILE_NOT_FOUND", "TIFF file not found at $tiffFilePath")
                return
            }

            val pngFilePaths = mutableListOf<String>()
            var pageIndex = 0
            val options = TiffBitmapFactory.Options()

            // Set a maximum limit to avoid infinite loops
            val maxPages = 100

            // Open the file descriptor
//                var maxPages = options.outCurDirectoryNumber
//                Log.d(TAG, "outCurDirectoryNumber $maxPages")
                // Loop through pages and decode
            var bitmap: Bitmap? = null;
                do {

                    ParcelFileDescriptor.open(tiffFile, ParcelFileDescriptor.MODE_READ_ONLY).use { parcelFileDescriptor ->

                        // Get the underlying integer file descriptor
                        val fileDescriptorInt: Int = parcelFileDescriptor.fd // Directly access the file descriptor
                        options.inDirectoryNumber = pageIndex
//                        options.outDirectoryCount = 2;
//                        Log.d(TAG, "Options: ${options.toLogString()}")
                        // Log.d(TAG, "Decoding page $pageIndex")
                        // Decode the current page using the integer file descriptor
                         bitmap = TiffBitmapFactory.decodeFileDescriptor(fileDescriptorInt, options)

//                        Log.d(TAG, "Bitmap height: ${bitmap.height}")
    //                    Log.d(TAG, "outCurDirectoryNumber1 $maxPages")
                        // Check if the bitmap is null, indicating no more pages

                        if(bitmap != null) {
                            // Save the bitmap as PNG
                            val outputFilePath = saveBitmapAsPng(bitmap!!, "$uuid-$pageIndex")
                            Log.d(TAG, "Saved PNG to: $outputFilePath")
                            pngFilePaths.add("file://$outputFilePath")
                        }
                    }
                    if (bitmap == null) {
                        Log.d(TAG, "No more pages to decode after page $pageIndex")
                        break // Exit loop if no more pages
                    }

                    pageIndex++ // Increment page index
                } while (1 < options.outDirectoryCount && pageIndex <= maxPages)

            // Handle the result
            if (pngFilePaths.isNotEmpty()) {
                val resultArray = WritableNativeArray()
                pngFilePaths.forEach { resultArray.pushString(it) }
                promise.resolve(resultArray)
            } else {
                promise.reject("NO_PAGES", "No valid pages found in the TIFF file")
            }

        } catch (e: Exception) {
            promise.reject("CONVERSION_ERROR", "Error converting TIFF to PNG: ${e.message}")
        }
    }

    private fun saveBitmapAsPng(bitmap: Bitmap, uuid: String): String {
        val outputDir = File(reactApplicationContext.cacheDir, "converted_images")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val pngFile = File(outputDir, "$uuid.png")
        FileOutputStream(pngFile).use { outStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        }

        return pngFile.absolutePath
    }

        // Extension function for logging options (simplified)
    private fun TiffBitmapFactory.Options.toLogString(): String {
        return "Options(outHeight=$outHeight, " +
                "inSampleSize=$inSampleSize, " +
                "inDirectoryNumber=$inDirectoryNumber, " +
                "outDirectoryCount=$outDirectoryCount, " +
                "inTempStorage=${outImageOrientation})"
    }
}
