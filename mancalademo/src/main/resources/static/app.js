const GameState = {
    "menu": 0,
    "playing" : 1
}

const url = 'http://localhost:8080';
const menuScreen = document.getElementById('screen-menu');
const playingScreen = document.getElementById('screen-playing');
const currentPlayer = document.getElementById('current-player');
const popDiv = document.getElementById('popDiv');

let currentGameState = GameState.menu;
let gameSessionId;
let playerName;
let playerTurnNow;
let stompClient;

const buttonOnline = document.getElementById('button-online');
buttonOnline.onclick = function() {
    postData(url + '/play/online', {})
    .then(payload => {
        console.log(payload);
        playerName = (payload.gameSession.gameStatusEnum === "NEW") ? payload.gameSession.firstPlayer  : playerName = payload.gameSession.secondPlayer;
        gameSessionId = payload.gameSession.sessionId;
        handlePayload(payload);
        connectToSocket(gameSessionId);
    });

    currentGameState = GameState.playing;
    renderGameScreen();
}

//const buttonLocal = document.getElementById('button-local');
//buttonLocal.onclick = function() {
//    // api call to server.
//    currentGameState = GameState.playing;
//    renderGameScreen();
//}

//const buttonComputer = document.getElementById('button-computer');
//buttonComputer.onclick = function() {
//    // api call to server.
//    currentGameState = GameState.playing;
//    renderGameScreen();
//}

const buttonQuit = document.getElementById('button-quit');
buttonQuit.onclick = function() {
    handlePayload({'payloadAction' : 'DISCONNECT'});
}

// Top pits.
const mancalaPitTop0 = document.getElementById('mancalapit-top-0');
mancalaPitTop0.onclick = function() {
    // api call to server.
    sowSelectedPit(0, 1);
}

const mancalaPitTop1 = document.getElementById('mancalapit-top-1');
mancalaPitTop1.onclick = function() {
    // api call to server.
    sowSelectedPit(1, 1);
}

const mancalaPitTop2 = document.getElementById('mancalapit-top-2');
mancalaPitTop2.onclick = function() {
    // api call to server.
    sowSelectedPit(2, 1);
}

const mancalaPitTop3 = document.getElementById('mancalapit-top-3');
mancalaPitTop3.onclick = function() {
    // api call to server.
    sowSelectedPit(3, 1);
}

const mancalaPitTop4 = document.getElementById('mancalapit-top-4');
mancalaPitTop4.onclick = function() {
    // api call to server.
    sowSelectedPit(4, 1);
}

const mancalaPitTop5 = document.getElementById('mancalapit-top-5');
mancalaPitTop5.onclick = function() {
    // api call to server.
    sowSelectedPit(5, 1);
}

const mancalaPitTop6 = document.getElementById('mancalapit-top-6');

// Bottom pits
const mancalaPitBottom0 = document.getElementById('mancalapit-bottom-0');
mancalaPitBottom0.onclick = function() {
    // api call to server.
    sowSelectedPit(0, 0);
}

const mancalaPitBottom1 = document.getElementById('mancalapit-bottom-1');
mancalaPitBottom1.onclick = function() {
    // api call to server.
    sowSelectedPit(1, 0);
}

const mancalaPitBottom2 = document.getElementById('mancalapit-bottom-2');
mancalaPitBottom2.onclick = function() {
    // api call to server.
    sowSelectedPit(2, 0);
}

const mancalaPitBottom3 = document.getElementById('mancalapit-bottom-3');
mancalaPitBottom3.onclick = function() {
    // api call to server.
    sowSelectedPit(3, 0);
}

const mancalaPitBottom4 = document.getElementById('mancalapit-bottom-4');
mancalaPitBottom4.onclick = function() {
    // api call to server.
    sowSelectedPit(4, 0);
}

const mancalaPitBottom5 = document.getElementById('mancalapit-bottom-5');
mancalaPitBottom5.onclick = function() {
    // api call to server.
    sowSelectedPit(5, 0);
}

const mancalaPitBottom6 = document.getElementById('mancalapit-bottom-6');

function renderGameScreen() {
    switch (currentGameState) {
        case 0:
            menuScreen.style.display = "block";
            playingScreen.style.display = "none";
            break;
        case 1:
            menuScreen.style.display = "none";
            playingScreen.style.display = "block";
            break;
    }
}

function renderGameBoard(data) {
    // render the gameboard.
    console.log(data);

    currentPlayer.innerText = (data.nextUp === playerName) ? "Your turn!" : "Opponents turn!";

    if (data.gameStatusEnum === "FINISHED") {
        if (data.mancalaPits[1][6] > data.mancalaPits[0][6]) {
            currentPlayer.innerText = "Blue player has won!";
        }
        else {
            currentPlayer.innerText = "Purple player has won!";
        }
    }
    else if (data.gameStatusEnum === "NEW") {
        currentPlayer.innerText = "Waiting for an opponent...";
    }

    // top (player 2)
    mancalaPitTop0.innerText = data.mancalaPits[1][0];
    mancalaPitTop1.innerText = data.mancalaPits[1][1];
    mancalaPitTop2.innerText = data.mancalaPits[1][2];
    mancalaPitTop3.innerText = data.mancalaPits[1][3];
    mancalaPitTop4.innerText = data.mancalaPits[1][4];
    mancalaPitTop5.innerText = data.mancalaPits[1][5];
    mancalaPitTop6.innerText = data.mancalaPits[1][6];

    // bottom (player 1)
    mancalaPitBottom0.innerText = data.mancalaPits[0][0];
    mancalaPitBottom1.innerText = data.mancalaPits[0][1];
    mancalaPitBottom2.innerText = data.mancalaPits[0][2];
    mancalaPitBottom3.innerText = data.mancalaPits[0][3];
    mancalaPitBottom4.innerText = data.mancalaPits[0][4];
    mancalaPitBottom5.innerText = data.mancalaPits[0][5];
    mancalaPitBottom6.innerText = data.mancalaPits[0][6];
}

async function postData(url = '', data = {}) {
    // Default options are marked with *
    const response = await fetch(url, {
            method: 'POST', // *GET, POST, PUT, DELETE, etc.
            mode: 'cors', // no-cors, *cors, same-origin
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            credentials: 'same-origin', // include, *same-origin, omit
            headers: {
            'Content-Type': 'application/json'
            // 'Content-Type': 'application/x-www-form-urlencoded',
            },
            redirect: 'follow', // manual, *follow, error
            referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
            body: JSON.stringify(data) // body data type must match "Content-Type" header
    });
    
    return response.json(); // parses JSON response into native JavaScript objects
}

function connectToSocket(_sessionId) {
    console.log("connecting to the game");
    let socket = new SockJS(url + "/sow");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to the frame: " + frame);
        stompClient.subscribe("/topic/game-progress/" + _sessionId, function (response) {
            let payload = JSON.parse(response.body);
            handlePayload(payload);
        })
    })
}

function sowSelectedPit(pitIndex, rowIndex) { 
    postData(url + '/play/sow', { 
        'sessionId' : gameSessionId,
        'player' : playerName,
        'pitIndex' : pitIndex,
        'rowIndex' : rowIndex
    })
    .then(payload => {
        console.log(payload);
        handlePayload(payload);
    });
}

function handlePayload(payload) {
    switch (payload.payloadAction) {
        case "SHOWERROR":
            pop(payload.errorMessage);
            break;
        case "PLAYGAME":
            renderGameBoard(payload.gameSession);
            break;
        case "DISCONNECT":
            if (stompClient !== null) {
                stompClient.disconnect(function() {
                    stompClient = null;
                  });
            }

            currentGameState = GameState.menu;
            renderGameScreen();
            break;
    }
}

function pop(message) {
    popDiv.innerText = message;
    popDiv.style.display = 'block';
    setTimeout(hide, 4000);
}

function hide() {
    popDiv.innerText = "";
    popDiv.style.display = 'none';
}

// LETS START SHALL WE!
renderGameScreen();