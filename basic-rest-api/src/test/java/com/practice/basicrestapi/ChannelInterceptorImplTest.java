package com.practice.springstomp.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;

class ChannelInterceptorImplTest {

  private ChannelInterceptorImpl channelInterceptor;

  @BeforeEach
  void init() {
    this.channelInterceptor = new ChannelInterceptorImpl();
  }

  private Map<String, List<String>> createNativeHeader(String jwt) {
    Map<String, List<String>> nativeHeaders = new HashMap<>();

    if (jwt != null) {
      nativeHeaders.put(ChannelInterceptorImpl.AUTHORIZATION, Arrays.asList(jwt));
    }

    return nativeHeaders;
  }

  private Message createMessage(String setJwt) {
    MessageHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setHeader("simpMessageType", SimpMessageType.CONNECT);
    accessor.setHeader("stompCommand", StompCommand.CONNECT);
    accessor.setHeader("nativeHeaders", createNativeHeader(setJwt));
    accessor.setLeaveMutable(true);

    return new GenericMessage("testPayload", accessor.getMessageHeaders());
  }

  @Test
  public void userMustBeAuthorizedIfValidJwtIsPresentInStompHeader() {
    Message message = channelInterceptor
        .preSend(createMessage("jwtPresent"), new ExecutorSubscribableChannel());

    final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
        message,
        StompHeaderAccessor.class);

    Assertions.assertNotNull(accessor.getUser());
  }
}
