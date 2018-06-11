package edu.steam.cms.account.model;

public class UserAuth {
	
	private String id;
	private String userId;
	private String authId;
	private String authService;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAuthId() {
		return authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
	}
	public String getAuthService() {
		return authService;
	}
	public void setAuthService(String authService) {
		this.authService = authService;
	}

}
