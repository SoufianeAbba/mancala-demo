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
	@Autowired
	GameSessionRepository gameSessionRepository;
	
	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;
	
	@PostMapping("/online")
	public ResponseEntity<Payload> createOrJoinGameSession() {
		GameSession gameSession = gameSessionRepository.findByGameStatusEnum(GameStatusEnum.NEW);
		Payload payload = new Payload();
		
		if (gameSession == null) {
			String sessionId = UUID.randomUUID().toString();
			String firstPlayer = UUID.randomUUID().toString();
			int numberOfSmallPitsPerSide = 6;
			int numberOfStonesPerPit = 6;
			gameSession = gameSessionRepository.save(new GameSession(sessionId, firstPlayer, numberOfSmallPitsPerSide, numberOfStonesPerPit));
			payload.setPayloadAction(PayloadActionEnum.PLAYGAME);
			payload.setGameSession(gameSession);
		}
		else {
			String secondPlayer = UUID.randomUUID().toString();
			gameSession.setSecondPlayer(secondPlayer);
			gameSession.setGameStatusEnum(GameStatusEnum.INPROGRESS);
			gameSessionRepository.save(gameSession);
			payload.setPayloadAction(PayloadActionEnum.PLAYGAME);
			payload.setGameSession(gameSession);

			simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameSession.getSessionId(), payload);
		}

		return new ResponseEntity<>(payload, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/sow")
    public ResponseEntity<Payload> sow(@RequestBody Sow sow) {
		GameSession gameSession = gameSessionRepository.findBySessionId(sow.getSessionId());
		Payload payload = new Payload();

		if (gameSession.getGameStatusEnum() == GameStatusEnum.NEW) {
			payload.setErrorMessage("It is not possible to make a move. The game is still waiting for an opponent.");
			payload.setPayloadAction(PayloadActionEnum.SHOWERROR);
		}
		else if (gameSession.getGameStatusEnum() == GameStatusEnum.FINISHED) {
			payload.setErrorMessage("It is not possible to make a move. The game is finished");
			payload.setPayloadAction(PayloadActionEnum.SHOWERROR);
		}
		else if (!Objects.equals(gameSession.getNextUp(), sow.getPlayer())) {
			payload.setErrorMessage("It is not your turn yet.");
			payload.setPayloadAction(PayloadActionEnum.SHOWERROR);
		}
		else if (!gameSession.CheckIfPlayerSelectedOwnPit(sow)) {
			payload.setErrorMessage("Select your own pit!");
			payload.setPayloadAction(PayloadActionEnum.SHOWERROR);
		}
		else if (gameSession.CheckIfPlayerSelectedOwnPit(sow) && gameSession.CheckIfPlayerSelectedAnEmptyPit(sow)) {
			payload.setErrorMessage("You selected an empty pit! Try again!");
			payload.setPayloadAction(PayloadActionEnum.SHOWERROR);
		}
		else {
			gameSession.sow(sow);
			gameSession = gameSessionRepository.save(gameSession);
			payload.setGameSession(gameSession);
			payload.setPayloadAction(PayloadActionEnum.PLAYGAME);
			simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameSession.getSessionId(), payload);
		}

        return new ResponseEntity<>(payload, HttpStatus.ACCEPTED);
    }
}
