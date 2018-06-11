package edu.steam.cms.content.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import edu.steam.cms.content.model.Content;


@Mapper
public interface ContentMapper {

	@Select("SELECT ID AS id, NAME AS name, PRODUCT as productName, CATEGORY1 as category1, CATEGORY2 as category2, UPLOAD_BASE as uploadBase, IMAGE_CNT as imageCnt, IMAGES as images FROM CONTENT WHERE ID=#{id}")
	Content getContentInfo(String id);
	
	
	@Insert("INSERT INTO CONTENT(ID, NAME, PRODUCT, CATEGORY1, CATEGORY2, UPLOAD_BASE, IMAGE_CNT, IMAGES) "
			+ "VALUES  (#{id}, #{name}, #{productName}, #{category1}, #{category2}, #{uploadBase}, #{imageCnt}, #{images})")
	@SelectKey(statement="select 'CONTENT_'|| SEQ_CONTENT.nextval FROM DUAL", keyProperty="id", before=true, resultType=String.class)
	void insertContent(Content content);
	
	@Insert("UPDATE CONTENT SET NAME=#{name}"
			+ ", PRODUCT=#{productName}"
			+ ", CATEGORY1=#{category1}"
			+ ", CATEGORY2=#{category2}"
			+ ", UPLOAD_BASE=#{uploadBase}"
			+ ", IMAGE_CNT=#{imageCnt}"
			+ ", IMAGES=#{images}"
			+ " WHERE ID=#{id}")
	void updateContent(Content content);
	
	@Delete("DELETE CONTENT WHERE ID=#{id}")
	void deleteContent(@Param("id") String id);
}
