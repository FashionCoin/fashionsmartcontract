package fashion.coin.wallet.back.messenger.ws;


import fashion.coin.wallet.back.messenger.service.ChatMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class WsChatHandler extends AbstractWebSocketHandler {

    Logger logger = LoggerFactory.getLogger(WsChatHandler.class);
    static final ConcurrentLinkedQueue<WebSocketSession> connectionList = new ConcurrentLinkedQueue<WebSocketSession>();

    final
    ChatMessageService chatMessageService;

    public WsChatHandler(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        logger.info("Connection " + session.getId() + " established");
        connectionList.add(session);
        logger.info("This: " + this);
        logger.info("Connection List: " + connectionList);
        for (WebSocketSession connection : connectionList) {
            System.out.println(connection.getId() + "\t" + connection.isOpen());
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String msg = message.getPayload().toString();
        logger.info("Session " + session.getId() + ". Message: " + msg);

        logger.info(String.valueOf(chatMessageService));
        logger.info(String.valueOf(session));
        logger.info(msg);

        chatMessageService.subscribe(session, msg);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.info("WS transport error. Session: " + session.getId());
        purgeConnectionList();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Coonection " + session.getId() + " closed");
        purgeConnectionList();
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        logger.info("Pong session " + session.getId() + " msg: " + message.toString());
    }

    void purgeConnectionList() {
        List<WebSocketSession> forDelete = new ArrayList<>();
        for (WebSocketSession connection : connectionList) {
            if (!connection.isOpen()) {
                forDelete.add(connection);
            }
        }
        connectionList.removeAll(forDelete);
        connectionList.removeIf(Objects::isNull);
    }


    public void sendEventMessage() {

        logger.info("This: " + this);
        logger.info("Connection List: " + connectionList);
        logger.info("Connection List size: " + connectionList.size());
        for (WebSocketSession connection : connectionList) {
            if (connection.isOpen()) {
                logger.info("Try send " + connection.getId());
                try {
                    connection.sendMessage(new TextMessage("Message from backend"));
                    logger.info("Message to " + connection.getId() + " sent");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(connection.getId() + "\t" + connection.isOpen());
            }
        }
    }


}
