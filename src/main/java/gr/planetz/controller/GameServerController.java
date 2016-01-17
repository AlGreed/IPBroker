package gr.planetz.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import gr.planetz.Schema;
import gr.planetz.cache.CacheService;
import gr.planetz.model.GameMessage;
import gr.planetz.model.JoinToRoomRequest;
import gr.planetz.model.JoinToRoomResponse;

@RestController
public class GameServerController {

    private static final Logger LOG = Logger.getLogger(GameServerController.class);

    @Autowired
    private CacheService        cacheService;

    @Autowired
    private RestTemplate        restTemplate;

    @ResponseBody
    @RequestMapping(value = Schema.V1_0.JOIN_PATH, method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JoinToRoomResponse> handleJoinToRoomRequest(@Valid @RequestBody final JoinToRoomRequest jsonRequest, final HttpServletRequest request) {
        LOG.info("RCVD: a join request: " + jsonRequest);
        this.cacheService.putPlayer(jsonRequest.getNickname(), jsonRequest.getAddress());
        final JoinToRoomResponse response = new JoinToRoomResponse(this.cacheService.getPlayers());
        LOG.info("SEND: the list of players: " + response.getPlayers());
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @ResponseBody
    @RequestMapping(value = Schema.V1_0.GAME_PATH, method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity sendMessageTo(@Valid @RequestBody final GameMessage jsonRequest, final HttpServletRequest request) {
        LOG.info("RCVD: a game communication request: " + request);
        if (this.cacheService.getPlayers().containsKey(jsonRequest.getNickname())) {
            LOG.info("SEND: a game communication message to " + jsonRequest.getNickname());
            final ResponseEntity<GameMessage> jsonResponse = this.restTemplate.postForEntity(URI.create(this.cacheService.getPlayers().get(jsonRequest.getNickname())), jsonRequest.getMessage(),
                    GameMessage.class);
            return jsonResponse;
        }
        return new ResponseEntity<>("Not existing player!", HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @RequestMapping(value = Schema.V1_0.PLAYERS_PATH, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JoinToRoomResponse> getPlayers(final HttpServletRequest request) {
        LOG.info("RCVD: getting Players: " + request);
        return new ResponseEntity<>(new JoinToRoomResponse(this.cacheService.getPlayers()), HttpStatus.ACCEPTED);
    }

    private ResponseEntity createErrorResponseEntity(final HttpServletRequest request) {
        LOG.warn("Got an error on request: " + request.toString());
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(final HttpServletRequest request) {
        return createErrorResponseEntity(request);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(final HttpServletRequest request) {
        return createErrorResponseEntity(request);
    }

    @ResponseBody
    @RequestMapping(consumes = MediaType.ALL_VALUE)
    public ResponseEntity handleInvalidMediaType(final HttpServletRequest request) {
        return createErrorResponseEntity(request);
    }
}
