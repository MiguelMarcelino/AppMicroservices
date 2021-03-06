package io.App.EventService.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.App.EventService.EventComponent.Event;
import io.App.EventService.EventComponent.EventCatalog;
import io.App.EventService.EventComponent.Role;
import io.App.EventService.EventComponent.UserCredentialCheck;
import io.App.EventService.dto.EventDTO;
import io.App.EventService.dto.EventListWrapper;
import io.App.EventService.dto.Pair;
import io.App.EventService.exceptions.EventAlreadyExistsException;
import io.App.EventService.exceptions.InternalAppException;
import io.App.EventService.exceptions.UserDoesNotExistException;

@RestController
@RequestMapping("/eventApi")
public class EventCatalogController {

	@Autowired
	private EventCatalog eC;
	@Autowired
	private UserCredentialCheck uCC;

	private static final String INTERNAL_APP_ERROR_MESSAGE = "Internal Application Error";

	@GetMapping("/events")
	public EventListWrapper eventList() {
		return this.eC.getAllEvents();
	}

	@GetMapping("getEventsFromCommunity/{cID}")
	public ResponseEntity<Pair<String, EventListWrapper>> eventsFromCommunity(
			@PathVariable("cID") int cID) {
		EventListWrapper eLW = null;
		try {
			eLW = this.eC.getEventsFromCommunity(cID);
		} catch (InternalAppException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(new Pair<>(e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		System.out.println("Successfull events request");
		return new ResponseEntity<>(
				new Pair<>("Successfull events request", eLW), HttpStatus.OK);
	}

	@PostMapping(path = "event/create", consumes = {
			"application/json" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> registerNewEvent(
			@RequestBody String eventJSON) {
		ObjectMapper objectMapper = new ObjectMapper();
		EventDTO eDTO = null;

		try {
			eDTO = objectMapper.readValue(eventJSON, EventDTO.class);
			Role userRole = this.uCC.getUserRole(eDTO.getOwnerUserName());
			if (userRole.equals(Role.EDITOR) || userRole.equals(Role.ADMIN)) {
				this.eC.registerNewEvent(
						new Event(eDTO.getName(), eDTO.getStart(),
								eDTO.getEnd(), Integer.parseInt(eDTO.getcID()),
								eDTO.getcName(), eDTO.getOwnerUserName()));
			}
		} catch (JsonParseException | JsonMappingException
				| NumberFormatException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(INTERNAL_APP_ERROR_MESSAGE,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(INTERNAL_APP_ERROR_MESSAGE,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (UserDoesNotExistException | EventAlreadyExistsException
				| InternalAppException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
		}

		System.out.println("Successfully registered new Event");
		return new ResponseEntity<>("Successfully registered new Event",
				HttpStatus.CREATED);
	}

	@PostMapping("event/delete")
	public ResponseEntity<String> deleteEvent(@RequestBody String eventJSON) {
		ObjectMapper objectMapper = new ObjectMapper();
		EventDTO eDTO = null;

		try {
			eDTO = objectMapper.readValue(eventJSON, EventDTO.class);
			this.eC.deleteEvent(new Event(Integer.parseInt(eDTO.getId()),
					eDTO.getName(), eDTO.getStart(), eDTO.getEnd(),
					Integer.parseInt(eDTO.getcID()), eDTO.getcName(),
					eDTO.getOwnerUserName()));
		} catch (JsonParseException | JsonMappingException
				| NumberFormatException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(INTERNAL_APP_ERROR_MESSAGE,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(INTERNAL_APP_ERROR_MESSAGE,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InternalAppException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
		}

		System.out.println("Successfully deleted the Event");
		return new ResponseEntity<>("Successfully deleted the Event",
				HttpStatus.OK);
	}
}
