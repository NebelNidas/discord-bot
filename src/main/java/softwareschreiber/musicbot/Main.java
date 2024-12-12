package softwareschreiber.musicbot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.tinylog.Logger;

public class Main {
	public static void main(String[] args) {
		String token = "MTMxNjU1ODQ1MzU4NTQ3NzcyMw.Ge6hDr.4vFsZqJNHR9zL86pXhX71Y2Qc3D-xAsCgsVWZE";

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

		api.addMessageCreateListener(new UserInfoCommand());
		api.addMessageCreateListener(new VoiceChatJoinCommand(api));
		api.addMessageComponentCreateListener(new VoiceChatJoinCommand(api));

		api.addServerJoinListener(event -> Logger.info("Joined server " + event.getServer().getName()));
		api.addServerLeaveListener(event -> Logger.info("Left server " + event.getServer().getName()));
	}
}
