package softwareschreiber.musicbot;

import java.util.Optional;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.javacord.api.listener.message.MessageCreateListener;
import org.tinylog.Logger;

class MaggaCommand implements MessageCreateListener, MessageComponentCreateListener {
	private static final String JA = "ja";
	private static final String NEIN = "nein";

	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		if (event.getMessageContent().equalsIgnoreCase("!join")) {
			new MessageBuilder()
					.setContent("Soll ich dem Voicechat beitreten?")
					.addComponents(ActionRow.of(
							Button.success(JA, "Ja"),
							Button.secondary(NEIN, "Nein")))
					.send(event.getChannel());
		}
	}

	@Override
	public void onComponentCreate(MessageComponentCreateEvent event) {
		MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();
		String customId = messageComponentInteraction.getCustomId();

		switch (customId) {
			case JA:
				Optional<ServerVoiceChannel> channel = messageComponentInteraction.getUser().getConnectedVoiceChannel(messageComponentInteraction.getServer().get());

				if (channel.isPresent()) {
					channel.get().connect().thenAccept(audiConnection -> {
						//Audio Connection here
					}).exceptionally(e -> {
						Logger.error(e);
						return null;
					});
				}

				break;
			case NEIN:
				messageComponentInteraction.createImmediateResponder()
						.setContent("Na gut dann nicht")
						.respond();
				break;
		}
	}
}
