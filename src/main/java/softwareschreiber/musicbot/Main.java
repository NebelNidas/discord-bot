package softwareschreiber.musicbot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		// The token is the first argument of the program
		String token = "MTMxNjU1ODQ1MzU4NTQ3NzcyMw.Ge6hDr.4vFsZqJNHR9zL86pXhX71Y2Qc3D-xAsCgsVWZE";

		// We login blocking, just because it is simpler and doesn't matter here
		// Also we need all intents to get message content and user activities.
		DiscordApi api = new DiscordApiBuilder().setToken(token).setIntents(Intent.GUILD_MESSAGES, Intent.GUILDS, Intent.MESSAGE_CONTENT).login().join();

		// Print the invite url of the bot
		logger.info("You can invite me by using the following url: " + api.createBotInvite());

		// Add listeners
		api.addMessageCreateListener(new UserInfoCommand());

		// Log a message, if the bot joined or left a server
		api.addServerJoinListener(event -> logger.info("Joined server " + event.getServer().getName()));
		api.addServerLeaveListener(event -> logger.info("Left server " + event.getServer().getName()));
	}
}
