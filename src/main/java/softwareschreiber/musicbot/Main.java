package softwareschreiber.musicbot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.tinylog.Logger;

public class Main {
	public static void main(String[] args) {
		if (args.length != 1) {
			Logger.error("Please provide exactly one argument: the bot token");
			return;
		}

		String token = args[0];
		DiscordApi api = new DiscordApiBuilder()
				.setToken(token)
				.setIntents(
						Intent.GUILDS,
						Intent.GUILD_MESSAGES,
						Intent.MESSAGE_CONTENT,
						Intent.GUILD_VOICE_STATES)
				.login()
				.join();

		Logger.info("You can invite me by using the following url: " + api.createBotInvite());

		api.addMessageCreateListener(new VoiceChatJoinCommand(api));

		api.addServerJoinListener(event -> Logger.info("Joined server " + event.getServer().getName()));
		api.addServerLeaveListener(event -> Logger.info("Left server " + event.getServer().getName()));
	}
}
