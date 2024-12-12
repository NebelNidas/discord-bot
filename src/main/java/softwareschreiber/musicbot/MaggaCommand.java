package softwareschreiber.musicbot;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.javacord.api.listener.message.MessageCreateListener;

class MaggaCommand implements MessageCreateListener, MessageComponentCreateListener {
	private static final String JA = "success";
	private static final String THORSTEN = "thorsten";
	private static final String NEIN = "danger";
	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		if (event.getMessageContent().equalsIgnoreCase("!magga")) {
			new MessageBuilder()
					.setContent("Bist du ein Magga?")
					.addComponents(ActionRow.of(
							Button.success(JA, "Ja, bin ich"),
							Button.success(THORSTEN, "Seh ich aus wie Thorsten???"),
							Button.danger(NEIN, "Nein")))
					.send(event.getChannel());
		}
	}

	@Override
	public void onComponentCreate(MessageComponentCreateEvent event) {
		MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();
		String customId = messageComponentInteraction.getCustomId();

		switch (customId) {
			case JA:
				messageComponentInteraction.createImmediateResponder()
						.setContent("Braver Junge")
						.respond();
				break;
			case NEIN:
				messageComponentInteraction.createImmediateResponder()
						.setContent("Du bist ein Lappen")
						.respond();
				break;
			case THORSTEN:
				messageComponentInteraction.createImmediateResponder()
						.setContent("OBERMAGGA SERVUS")
						.respond();
				break;
		}
	}
}
