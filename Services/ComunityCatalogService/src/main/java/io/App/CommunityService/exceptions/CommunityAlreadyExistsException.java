package io.App.CommunityService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT, reason="This community already exists")
public class CommunityAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public CommunityAlreadyExistsException(String cName) {
		super("The community with the name '" + cName + "' already exists.");
	}


}
