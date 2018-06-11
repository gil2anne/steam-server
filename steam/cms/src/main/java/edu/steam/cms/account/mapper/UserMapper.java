package edu.steam.cms.account.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import edu.steam.cms.account.model.User;
import edu.steam.cms.account.model.UserAuth;
import edu.steam.cms.account.model.UserRole;

@Mapper
public interface UserMapper {
	
	@Select("SELECT * FROM USER WHERE ID=#{id}")
	User getUserInfo(String id);
	
	@Select("SELECT ID FROM USER WHERE EMAIL = #{email}")
	User getUserByEmail(String email);
	
	@Insert("INSERT INTO USER (ID, NAME, PASSWORD, EMAIL, MOBILE) VALUES(#{id}, #{name}, #{password}, #{email}, #{mobile})")
	void insertUserInfo(User user);
	
	@Insert("INSERT INTO USER_ROLE (USER_ID, USER_ROLE) VALUES (#{userId}, #{role})")
	void insertUserRole(@Param("userId") String userId, @Param("role") String role);
	
	@Select("SELECT R.USER_ROLE AS userRole FROM USER_ROLE R LEFT JOIN USER U ON U.ID = R.USER_ID WHERE U.ID = #{id}")
	List<UserRole> getRoleList(String id);

	@Select("SELECT U.ID, U.NAME, U.EMAIL, U.MOBILE FROM USER U LEFT JOIN USER_AUTH O ON U.ID = O.USER_ID WHERE O.AUTH_ID = #{authId} AND O.AUTH_PROD = #{authProd}")
	User getUserByOAuth(@Param("authId") String authId, @Param("authProd") String authProd);
	
	@Insert("INSERT INTO USER_AUTH (USER_ID, AUTH_ID, AUTH_SERVICE) VALUES (#{userId}, #{authId}, #{authService})")
	void insertUserOAuth(UserAuth auth);
}
