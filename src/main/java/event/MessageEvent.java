package event;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ServiceClient;

public class MessageEvent {

        private static final Logger LOG = LoggerFactory.getLogger(MessageEvent.class);
        private static final String START_MESSAGE = "!start";
        private static final String STOP_MESSAGE = "!stop";
        private final ServiceClient.UserService userService;

        public MessageEvent(SimpleEventHandler eventHandler, ServiceClient.UserService userService) {
            this.userService = userService;
            eventHandler.onEvent(ChannelMessageEvent.class, event -> onChannelMessage(event));
        }

        public void onChannelMessage(ChannelMessageEvent event) {
            var hasAdminRole = userService.getAdministratorUsers().stream()
                    .filter(adminUser -> event.getUser().getId().equals(adminUser.getUserId())).count() > 0;
            var isStartMessage = hasAdminRole &&  event.getMessage().trim().equals(START_MESSAGE);
            var isStopMessage = hasAdminRole && event.getMessage().trim().equals(STOP_MESSAGE);
            EventManager.getInstance().fireEvent(isStartMessage ? ApplicationEvents.START_GAME :
                    isStopMessage ? ApplicationEvents.STOP_GAME : ApplicationEvents.MESSAGE_RECEIVE, event);
            LOG.info(
                    "User {} {}- Message {}",
                    event.getUser().getName(),
                    event.getUser().getId(),
                    event.getMessage()
            );
        }
}
