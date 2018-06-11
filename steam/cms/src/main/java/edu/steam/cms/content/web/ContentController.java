package edu.steam.cms.content.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.steam.cms.content.ContentService;
import edu.steam.cms.content.model.Content;

@Controller
public class ContentController {

		
	@Autowired
	ContentService contentService;
	
	/**
	 * contentPath에 파일을 업로드함
	 * @param contentPath
	 * @param file
	 * @return
	 */
	@RequestMapping(value="/contents/{type}/upload", method=RequestMethod.POST, consumes="multipart/form-data")
	public ResponseEntity<Map<String, ?>> uploadContents(
			@PathVariable(name="type") String type, 
    		@RequestParam(name="contentPath", required=false) String contentPath,
    		@RequestParam("content") MultipartFile file) {
		
		if( StringUtils.isEmpty(contentPath) ) {
			contentPath = UUID.randomUUID().toString();;
		} 
		
		Path uploadPath = contentService.getContentPath(type, contentPath);
		
		Map<String, Object> data = new HashMap<>();
		
		/*data.put("uploadedImages", contentService.uploadContent(uploadPath, file));
		data.put("uploadBase", contentPath);*/
		
		String json = "{\"uploadedImages\":[\"0066792221.png\",\"0066792442.png\",\"0066792617.png\",\"0066792710.png\",\"0066792806.png\",\"0066792911.png\",\"0066793000.png\",\"0066793082.png\",\"0066793188.png\",\"0066793296.png\",\"0066793388.png\",\"0066793483.png\",\"0066793572.png\",\"0066793662.png\",\"0066793752.png\",\"0066793839.png\",\"0066793931.png\",\"0066794019.png\",\"0066794099.png\",\"0066794188.png\",\"0066794286.png\",\"0066794378.png\",\"0066794465.png\",\"0066794559.png\",\"0066794666.png\",\"0066794765.png\",\"0066794851.png\",\"0066794918.png\",\"0066794992.png\",\"0066795088.png\",\"0066795192.png\",\"0066795468.png\"],\"uploadBase\":\"4e39cd9c-fbe9-4f92-a5cc-559473767419\"}";
		try {
			data = new ObjectMapper().readValue(json, Map.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return new ResponseEntity<Map<String,?>>(data, HttpStatus.CREATED);
    }
	
	/**
	 * 신규 혹은 변경된 정보를 저장
	 * @param content
	 * @return
	 */
	@RequestMapping(value="/contents/model", method=RequestMethod.POST, consumes="application/json")
	public ResponseEntity<String> saveContents(@RequestBody Content content) {
		
		System.out.println(content);
		
		contentService.saveContent(content);
		return new ResponseEntity<String>("", HttpStatus.OK);
	}
	
	/**
	 * id로 업로드된 contents를 조회함
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/contents/{type}/{id}", method=RequestMethod.GET)
	public ResponseEntity<Content> retrieveContent(
			@PathVariable(name="type", required=true) String type,
			@PathVariable(name="id", required=true) String id) throws IOException {
		
		Content content = contentService.getContent(id);
		return new ResponseEntity<Content>(content, HttpStatus.OK);
	}
}
