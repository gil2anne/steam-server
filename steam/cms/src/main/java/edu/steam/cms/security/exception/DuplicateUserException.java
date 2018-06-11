package edu.steam.cms.security.exception;

public class DuplicateUserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4748636124928293045L;

	public DuplicateUserException() {
		super();
	}
	
	public DuplicateUserException(String msg) {
		super(msg);
	}
	
}
