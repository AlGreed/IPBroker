package gr.planetz.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import gr.planetz.Schema;
import gr.planetz.cache.CacheService;
import gr.planetz.model.JoinToRoomRequest;
import gr.planetz.model.JoinToRoomResponse;

@RestController
public class GameServerController {

    private static final Logger LOG = Logger.getLogger(GameServerController.class);

    @Autowired
    private CacheService        cacheService;

    @ResponseBody
    @RequestMapping(value = Schema.V1_0.JOIN_PATH, method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JoinToRoomResponse> handleJoinToRoomRequest(@Valid @RequestBody final JoinToRoomRequest jsonRequest, final HttpServletRequest request) {
        LOG.info("RCVD: a join request: " + jsonRequest);
        this.cacheService.putPlayer(jsonRequest.getNickname(), request.getRemoteAddr());
        final JoinToRoomResponse response = new JoinToRoomResponse(this.cacheService.getPlayers());
        LOG.info("SEND: the list of players: " + response.getPlayers());
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @ResponseBody
    @RequestMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity handleInvalidHttpMethod(final HttpServletRequest request) {
        return createErrorResponseEntity(request);
    }

    private ResponseEntity createErrorResponseEntity(final HttpServletRequest request) {
        LOG.warn("Got an error on request: " + request);
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
