package edu.steam.cms.content.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Content {
	/**
	 * PK
	 */
	private String id;

	/**
	 * 컨텐츠 명
	 */
	private String name;
	
	/**
	 * 제품명 : LEGO
	 */
	private String productName;
	
	/**
	 * 카테고리 기준 (2 레벨중 어떤게 올지 모름)
	 */
	private String category1;
	
	/**
	 * 카테고리 기준
	 */
	private String category2;
	
	/**
	 * 난이도
	 */
	private String level;
	
	/**
	 * 이미지 콘텐츠 파일 저장 위치
	 */
	private String uploadBase;
	
	/**
	 * 가이드 이미지 파일 갯수
	 */
	private int imageCnt;
	
	/**
	 * 선택된 이미지 파일 업로드/노출 순서
	 */
	private List<String> imageList;
	
	public Content() {
		this.imageList = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCategory1() {
		return category1;
	}

	public void setCategory1(String category1) {
		this.category1 = category1;
	}

	public String getCategory2() {
		return category2;
	}

	public void setCategory2(String category2) {
		this.category2 = category2;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getUploadBase() {
		return uploadBase;
	}

	public void setUploadBase(String uploadBase) {
		this.uploadBase = uploadBase;
	}

	public int getImageCnt() {
		return imageCnt;
	}

	public void setImageCnt(int imageCnt) {
		this.imageCnt = imageCnt;
	}

	public List<String> getImageList() {
		return imageList;
	}

	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}

	/**
	 * DB 매핑
	 * @return
	 */
	@JsonIgnore
	public String getImages() {
		
		if( this.imageList != null ) {
			return String.join(",", this.imageList);
		}
		return "";
	}
	
	public void setImages(String images) {
		if( !StringUtils.isEmpty(images)) {
			this.imageList = Arrays.asList(images.split(","));
		}
	}
}
