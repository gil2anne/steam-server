package edu.steam.cms.content.web;

public class ContentException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3634631314689716742L;
	
	public ContentException(String message) {
		super(message);
	}

	public ContentException(String message, Throwable e) {
		super(message, e);
	}
}
