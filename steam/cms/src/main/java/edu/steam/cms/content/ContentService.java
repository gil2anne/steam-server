package edu.steam.cms.content;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import edu.steam.cms.content.mapper.ContentMapper;
import edu.steam.cms.content.model.Content;
import edu.steam.cms.content.model.ContentContracts;
import edu.steam.cms.content.web.ContentException;
import edu.steam.cms.util.FileUtil;

@Service
public class ContentService {

	/**
	 * pdf 변환용
	 */
    private static final int IMAGE_DPI = 300;
    private static final int THUMBNAIL_DPI = 30;
    
    private static final float MIDDLE_SIZE = 2000f;
    
	@Value("${upload.base.location}")
    private String uploadBase;
	
	@Autowired
	private ContentMapper contentDao;
	
	/**
	 * content 저장
	 * @param content
	 */
	public String saveContent(Content content) {
		contentDao.insertContent(content);
		
		/*if( StringUtils.isEmpty(content.getId())) {
			contentDao.insertContent(content);
		} else {
			contentDao.updateContent(content);
		}*/
		return content.getId();
	}
	
	/**
	 * content 정보 조회
	 * @param contentId
	 * @return
	 */
	public Content getContent(String contentId) {
		return contentDao.getContentInfo(contentId);
	}
	
	/**
	 * type아래 업로드할 디렉터리를생성
	 * type은 레고, .. 
	 * @param type
	 * @return
	 */
	public Path createContentPath(String type) {
		
		String folderName = UUID.randomUUID().toString();
		return getContentPath(type, folderName);
	}
	
	public Path getContentPath(String type, String folderName) {
		return Paths.get(uploadBase, type, folderName);
	}
	
	/**
	 * 콘텐츠 이미지를 업로드할 폴더 조회
	 * id가 없는 경우 매번 채번됨으로 
	 * @param id
	 * @return
	 */
	public Path getContentPathById(String id) {
		
		if( StringUtils.isEmpty(id)) {
			throw new ContentException("content does not exists " + id);
		} 
		
		//타입에 따라서 분기해야 함..
		Content content = contentDao.getContentInfo(id);
		
		if( content == null ) {
			return null;
		}
		
		String folderName = content.getUploadBase();
		//TODO contents종류가 model, promotion, ugc 등이 있음 
		return getContentPath("model", folderName );
	}
	
	/**
	 * 콘텐트 이미지나 pdf를 업로드한다.
	 * @param folder
	 * @param file
	 * @return
	 */
	public List<String> uploadContent(Path uploadPath, MultipartFile file) {
		
		 if( FileUtil.isImageFile(file.getContentType())) {
			 try {
				 return Arrays.asList(saveImg(uploadPath, file));
			 }catch(Exception e) {
				 e.printStackTrace();
			 }
		} else if( FileUtil.isPdfFile(file.getContentType())) {
			
			return savePdf(uploadPath, file);
		}
		 
		 return Collections.EMPTY_LIST;
	}
	
	/**
	 * 업로드 파일이 유효한지 조회한다.
	 * @param file
	 * @return
	 * @throws ContentException
	 */
	private String validateUploadFile(MultipartFile file) throws ContentException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        if (file.isEmpty()) {
            throw new ContentException("Failed to store empty file " + fileName);
        }

        // This is a security check
        if (fileName.contains("..")) {
            throw new ContentException(
                    "Cannot store file with relative path outside current directory "
                            + fileName);
        }
        return fileName;
	}
	
	/**
	 * pdf원본 파일을 저장하고 png파일로 변환, 변환된 이미지 목록을 반환한다.
	 * thumbnail 이미지를 생성한다.
	 * @param content
	 * @param sizeOfcontent
	 * @param file
	 * @return
	 * @throws ContentException
	 */
	private List<String> savePdf(Path uploadPath, MultipartFile file) throws ContentException {

        String fileName = validateUploadFile(file);
        
        //1. 디렉터리 생성
        if( !uploadPath.toFile().exists() && !uploadPath.toFile().mkdirs() ) {
        	throw new ContentException("thumb path can not create " + uploadPath);
        }
        
        Path thumbPath = uploadPath.resolve("thumb");
        if( !thumbPath.toFile().exists() && !thumbPath.toFile().mkdir()) {
        	throw new ContentException("thumb path can not create " + thumbPath);
        }
        
        List<String> uploadedFileNames = new ArrayList<>();

        BufferedInputStream fin = null;
        try {
        	//1. copy
        	Path copy = uploadPath.resolve(fileName);
            fin = new BufferedInputStream(file.getInputStream());      
            
        	Files.copy(fin, uploadPath.resolve(fileName), 
            		StandardCopyOption.REPLACE_EXISTING);
        	
        	//2. pdf to png
        	final PDDocument document  = PDDocument.load(copy.toFile());
        	try {
            	PDFRenderer pdfRenderer = new PDFRenderer(document);
        		PDRectangle rectangle = null;
        		PDPage page = null;

        		for (int i = 0; i < document.getNumberOfPages(); ++i) {
        			
        			BufferedImage bim = pdfRenderer.renderImageWithDPI(i, IMAGE_DPI, ImageType.ARGB);
        			
        			fileName = FileUtil.generateUniqueFileName("png");
        			ImageIOUtil.writeImage(bim,  
        					uploadPath.resolve(fileName).toString(), 
        					IMAGE_DPI);
        			
        			FileUtil.drawThumbnail(bim, "png", thumbPath.resolve(fileName));
/*        			//pdf내 각 페이지는 동일한 디멘전
        			if( rectangle == null ) {
        			   	int target = Math.min(bim.getWidth(), bim.getHeight()) * 90/100;
        		    	float scale = THUMBNAIL_SIZE/target;

        		    	int m1 = (bim.getWidth() - target)/2;
        		    	float left = (m1 + target + m1)/2 - target/2;
        		    	int m2 = (bim.getHeight() - target)/2;
        		    	float top = (m2 + target + m2)/2 - target/2;

        		        rectangle = new PDRectangle(left*scale, top*scale, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        			}
        			
        			page = document.getPage(i);
        			page.setMediaBox(rectangle);
        			page.setCropBox(rectangle);

        			bim = pdfRenderer.renderImageWithDPI(i, THUMBNAIL_DPI, ImageType.ARGB);        			
        			ImageIOUtil.writeImage(bim,  thumbPath.resolve(fileName).toString(), THUMBNAIL_DPI);
*/        			
        			uploadedFileNames.add(fileName);

        		}
        	} finally {
        		if( document != null ) 
        			try {
            			document.close();
            		}catch(IOException ioe2) {	            			
            		}
        	}

        } catch (IOException e) {
            throw new ContentException("Failed to store file " + fileName, e);
        } finally {
        	if( fin != null ) {
        		try {
					fin.close();
				} catch (IOException e) {
				}
        	}
        }

        return uploadedFileNames;
    }

	

	/**
	 * 이미지 파일을 업로드하고, 업로드된 파일의 가로크기를 비교하여, 
	 * 크면 일정 크기로 줄인다.
	 * 파일 구조는 대표 이미지는 contents/{uuid}.png, contents/{uuid}_thumb.png
	 * 모델 이미지는 contents/{uuid}/*.png, contents/{uuid}/*_thumb.png 
	 * @param file
	 * @param dest
	 * @throws IOException
	 */
    private String saveImg(Path uploadPath, MultipartFile file) throws IOException {

        validateUploadFile(file);        
        
        //1. 디렉터리 생성
        Path parent = uploadPath.getParent();
        String name = uploadPath.getFileName().toString();
        if( !parent.toFile().exists() && !parent.toFile().mkdirs() ) {
        	throw new ContentException("thumb path can not create " + parent);
        }
        
        //2. 원본 저장
        String ext = FileUtil.getExtensionForMime(file.getContentType());
        File img  = parent.resolve(name + "." + ext).toFile();
        ImageIO.write(ImageIO.read(file.getInputStream()), ext, img);

    	//thumbnail생성
    	FileUtil.drawThumbnail(
    			ImageIO.read(img), 
    			ext, 
    			parent.resolve(name + "_thumb." + ext));
        
        return "main.png";
    }
}
