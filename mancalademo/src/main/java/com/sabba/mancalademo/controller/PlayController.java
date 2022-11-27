package com.sabba.mancalademo.controller;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sabba.mancalademo.model.GameSession;
import com.sabba.mancalademo.model.GameStatusEnum;
import com.sabba.mancalademo.model.Payload;
import com.sabba.mancalademo.model.PayloadActionEnum;
import com.sabba.mancalademo.model.Sow;
import com.sabba.mancalademo.repository.GameSessionRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/play")
public class PlayController {
	GameSessionRepository gameSessionRepository;
	SimpMessagingTemplate simpMessagingTemplate;

	public PlayController(GameSessionRepository gameSessionRepository, SimpMessagingTemplate simpMessagingTemplate)
	{
		this.gameSessionRepository = gameSessionRepository;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@PostMapping("/online")
	public ResponseEntity<Payload> createOrJoinGameSession() {
		GameSession gameSession = gameSessionRepository.findByGameStatusEnum(GameStatusEnum.NEW);
		
		if (gameSession == null) {
			String sessionId = UUID.randomUUID().toString();
			String firstPlayer = UUID.randomUUID().toString();
			int numberOfSmallPitsPerSide = 6;
			int numberOfStonesPerPit = 6;
			gameSession = gameSessionRepository.save(new GameSession(sessionId, firstPlayer, numberOfSmallPitsPerSide, numberOfStonesPerPit));
			var payload = new Payload(gameSession, "", PayloadActionEnum.PLAYGAME);

			return new ResponseEntity<>(payload, HttpStatus.ACCEPTED);
		}
		else {
			String secondPlayer = UUID.randomUUID().toString();
			gameSession.setSecondPlayer(secondPlayer);
			gameSession.setGameStatusEnum(GameStatusEnum.INPROGRESS);
			gameSessionRepository.save(gameSession);
			var payload = new Payload(gameSession, "", PayloadActionEnum.PLAYGAME);

			simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameSession.getSessionId(), payload);
			return new ResponseEntity<>(payload, HttpStatus.ACCEPTED);
		}
	}
	
	@PostMapping("/sow")
    public ResponseEntity<Payload> sow(@RequestBody Sow sow) {
		GameSession gameSession = gameSessionRepository.findBySessionId(sow.getSessionId());

		if (gameSession.getGameStatusEnum() == GameStatusEnum.NEW) {
			var payload = new Payload(null,
					"It is not possible to make a move. The game is still waiting for an opponent.",
					PayloadActionEnum.SHOWERROR);
			return new ResponseEntity<>(payload, HttpStatus.ACCEPTED);
		}
		else if (gameSession.getGameStatusEnum() == GameStatusEnum.FINISHED) {
			var payload = new Payload(null,
					"It is not possible to make a move. The game is finished",
					PayloadActionEnum.SHOWERROR);
			return new ResponseEntity<>(payload, HttpStatus.ACCEPTED);
		}
		else if (!Objects.equals(gameSession.getNextUp(), sow.getPlayer())) {
			var payload = new Payload(null,
					"It is not your turn yet.",
					PayloadActionEnum.SHOWERROR);
			return new ResponseEntity<>(payload, HttpStatus.ACCEPTED);
		}
		else if (!gameSession.CheckIfPlayerSelectedOwnPit(sow)) {
			var payload = new Payload(null,
					"Select your own pit!",
					PayloadActionEnum.SHOWERROR);
			return new ResponseEntity<>(payload, HttpStatus.ACCEPTED);
		}
		else if (gameSession.CheckIfPlayerSelectedOwnPit(sow) && gameSession.CheckIfPlayerSelectedAnEmptyPit(sow)) {
			var payload = new Payload(null,
					"You selected an empty pit! Try again!",
					PayloadActionEnum.SHOWERROR);
			return new ResponseEntity<>(payload, HttpStatus.ACCEPTED);
		}
		else {
			gameSession.sow(sow);
			gameSession = gameSessionRepository.save(gameSession);
			var payload = new Payload(gameSession, "", PayloadActionEnum.PLAYGAME);

			simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameSession.getSessionId(), payload);
			return new ResponseEntity<>(payload, HttpStatus.ACCEPTED);
		}
    }
}
