#import "TiffConverter.h"
#import <React/RCTLog.h>
#import <React/RCTConvert.h>
#import <UIKit/UIKit.h>
#import <ImageIO/ImageIO.h>

@implementation TiffConverter

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(convertTiffToPng:(NSString *)tiffFilePath uuid:(NSString *)uuid resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  // Log the UUID for debugging purposes
  RCTLogInfo(@"Received UUID: %@", uuid);
  
  NSURL *tiffURL = [NSURL fileURLWithPath:tiffFilePath];
  CGImageSourceRef imageSource = CGImageSourceCreateWithURL((__bridge CFURLRef)tiffURL, NULL);
  
  if (!imageSource) {
    NSError *error = [NSError errorWithDomain:@"TiffConverterErrorDomain" code:1 userInfo:@{NSLocalizedDescriptionKey: @"Failed to load TIFF file."}];
    reject(@"load_error", @"Failed to load TIFF file", error);
    return;
  }
  
  size_t pageCount = CGImageSourceGetCount(imageSource);
  NSMutableArray *pngFilePaths = [NSMutableArray array];
  
  for (size_t i = 0; i < pageCount; i++) {
    CGImageRef imageRef = CGImageSourceCreateImageAtIndex(imageSource, i, NULL);
    if (!imageRef) {
      NSError *error = [NSError errorWithDomain:@"TiffConverterErrorDomain" code:2 userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Failed to load page %zu of TIFF file.", i]}];
      reject(@"load_error", [NSString stringWithFormat:@"Failed to load page %zu of TIFF file.", i], error);
      CFRelease(imageSource);
      return;
    }

    UIImage *image = [UIImage imageWithCGImage:imageRef];
    NSData *pngData = UIImagePNGRepresentation(image);
    
    if (!pngData) {
      NSError *error = [NSError errorWithDomain:@"TiffConverterErrorDomain" code:3 userInfo:@{NSLocalizedDescriptionKey: @"Failed to create PNG data."}];
      reject(@"conversion_error", @"Failed to create PNG data", error);
      CFRelease(imageRef);
      CFRelease(imageSource);
      return;
    }

    NSString *pngFilePath = [self generatePngFilePathForPage:i uuid:uuid];  // Pass the UUID here
    BOOL success = [pngData writeToFile:pngFilePath atomically:YES];
    
    if (!success) {
      NSError *error = [NSError errorWithDomain:@"TiffConverterErrorDomain" code:4 userInfo:@{NSLocalizedDescriptionKey: @"Failed to save PNG file."}];
      reject(@"save_error", @"Failed to save PNG file", error);
      CFRelease(imageRef);
      CFRelease(imageSource);
      return;
    }

    [pngFilePaths addObject:pngFilePath];
    CFRelease(imageRef);
  }

  CFRelease(imageSource);
  resolve(pngFilePaths);
}

// Updated method to include UUID in the file name
- (NSString *)generatePngFilePathForPage:(size_t)pageIndex uuid:(NSString *)uuid {
  NSString *documentsDirectory = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
  return [documentsDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_page_%zu.png", uuid, pageIndex]];
}

@end
