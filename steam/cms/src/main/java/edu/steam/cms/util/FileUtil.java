package edu.steam.cms.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.StringUtils;

import edu.steam.cms.content.web.ContentException;

public class FileUtil {
	
	public static final String MIME_APPLICATION_PDF       = "application/pdf";
	public static final String MIME_IMAGE_BMP         = "image/bmp";
	public static final String MIME_IMAGE_GIF         = "image/gif";
	public static final String MIME_IMAGE_JPEG          = "image/jpeg";
	public static final String MIME_IMAGE_PNG         = "image/png";
	public static final String MIME_VIDEO_QUICKTIME       = "video/quicktime";
	public static final String MIME_VIDEO_MPEG          = "video/mpeg";
	public static final String MIME_VIDEO_VND_MPEGURL     = "video/vnd.mpegurl";
	public static final String MIME_VIDEO_X_MSVIDEO       = "video/x-msvideo";
	public static final String MIME_VIDEO_X_MS_WMV        = "video/x-ms-wmv";

	private static HashMap<String, String> mimeTypeMapping;

	static {
	  mimeTypeMapping = new HashMap<String, String>(20) {
	    private void put1(String ext, String mime) {
	      if (put(mime, ext) != null) {
	        throw new IllegalArgumentException("Duplicated extension: " + ext);
	  }
	}
	{
		put1("avi", MIME_VIDEO_X_MSVIDEO);
		put1("wmv", MIME_VIDEO_X_MS_WMV);
		put1("bmp", MIME_IMAGE_BMP);
		put1("gif", MIME_IMAGE_GIF);
		put1("jpg", MIME_IMAGE_JPEG);
		put1("png", MIME_IMAGE_PNG);
		put1("mov", MIME_VIDEO_QUICKTIME);
		put1("mpeg", MIME_VIDEO_MPEG);
		put1("pdf", MIME_APPLICATION_PDF);
	  }};
	}
	
	private static final float THUMBNAIL_SIZE = 200f;
	
	public static String getExtensionForMime(String mimeType) {
    	
    	if( StringUtils.isEmpty(mimeType) || mimeType.indexOf('/') < 0 ) {
    		throw new ContentException("contentType cannot be recognized " + mimeType);
    	}
    	
    	if( !mimeTypeMapping.containsKey(mimeType) ) {
    		throw new ContentException("contentType cannot be recognized " + mimeType);
    	}
    	return mimeTypeMapping.get(mimeType);
    }

	public static boolean isImageFile(String contentType) {
		if (!(contentType.equals("image/pjpeg") || contentType.equals("image/jpeg") || contentType.equals("image/png")
				|| contentType.equals("image/gif") || contentType.equals("image/bmp")
				|| contentType.equals("image/x-png") || contentType.equals("image/x-icon"))) {
			return false;
		}
		return true;
	}
	
	public static boolean isPdfFile(String contentType) {
		
		if (!(contentType.equals("application/pdf") || contentType.equals("application/x-pdf"))) {
			return false;
		}
		return true;
	}
	
	static final long TIME_OF_DAY =1000 * 60 * 60 * 24L;

	/**
	 * TODO 좀더 나은 방법을 생각해보세요.
	 * @param ext
	 * @return
	 */
	public static String generateUniqueFileName(String ext) {
		
		//shift안하면 유사한게 나오나 shift하면 안나옮 왜????
	    int millis = (int)System.currentTimeMillis()>>>4;
	    return String.format("%010d.%s", millis, ext);
	}
	
	 /**
     * 입력된 buffered image의 thumbnail을 생성한다.
     * @param orginal
     * @param format
     * @param thumb
     * @throws IOException
     */
    public static void drawThumbnail(BufferedImage orginal, String format, Path thumb ) 
    		throws IOException {
    	
		int width = orginal.getWidth();
		int height = orginal.getHeight();

		//폭이 넓으면, 폭의 시작점을 조정
		int x1 = 0, y1 = 0, x2 = 0, y2 = 0;

		if( width > height ) {
			x1 = (width - height)/2;
			x2 = (width - x1);
			y2 = height;
		} else {
			y1 = (height - width)/2;
			y2 = (height - y1);
			x2 = width;
		}

		BufferedImage outputImage = new BufferedImage(
				(int)THUMBNAIL_SIZE,
				(int)THUMBNAIL_SIZE, 
				orginal.getType());

        Graphics2D g2d = outputImage.createGraphics();
        //destination from source
        g2d.drawImage(orginal, 0, 0, 200, 200, x1, y1, x2, y2, null);
        g2d.dispose();

		ImageIO.write(outputImage, format, thumb.toFile() );
    }
	
	
}
