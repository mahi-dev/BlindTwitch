package event;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageEvent {

        private static final Logger LOG = LoggerFactory.getLogger(MessageEvent.class);

        public MessageEvent(SimpleEventHandler eventHandler) {
            eventHandler.onEvent(ChannelMessageEvent.class, event -> onChannelMessage(event));
        }

        public void onChannelMessage(ChannelMessageEvent event) {
            LOG.info(
                    "Channel [%s] - User[%s] - Message [%s]%n",
                    event.getChannel().getName(),
                    event.getUser().getName(),
                    event.getMessage()
            );
        }
}
