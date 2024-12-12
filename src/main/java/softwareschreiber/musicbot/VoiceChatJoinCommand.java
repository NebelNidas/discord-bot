package softwareschreiber.musicbot;

import java.util.Locale;
import java.util.Optional;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
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

class VoiceChatJoinCommand implements MessageCreateListener, MessageComponentCreateListener {
	private static final String JA = "ja";
	private static final String NEIN = "nein";
	private final DiscordApi api;

	VoiceChatJoinCommand(DiscordApi api) {
		this.api = api;
	}

	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		switch (event.getMessageContent().toLowerCase(Locale.ROOT)) {
			case "!join":
				new MessageBuilder()
					.setContent("Soll ich dem Voicechat beitreten?")
					.addComponents(ActionRow.of(
							Button.success(JA, "Ja"),
							Button.secondary(NEIN, "Nein")))
					.send(event.getChannel());

				break;
		}
	}

	@Override
	public void onComponentCreate(MessageComponentCreateEvent event) {
		MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();
		String customId = messageComponentInteraction.getCustomId();

		switch (customId) {
			case JA:
				Optional<ServerVoiceChannel> channel = messageComponentInteraction.getUser()
						.getConnectedVoiceChannel(messageComponentInteraction.getServer().get());

				if (channel.isPresent()) {
					channel.get().connect().thenAccept(this::audioConnection).exceptionally(e -> {
						Logger.error(e);
						return null;
					});
				}

				break;
			case NEIN:
				messageComponentInteraction.createImmediateResponder()
						.setContent("Na gut dann")
						.respond();
				break;
		}
	}

	private void audioConnection(AudioConnection audioConnection) {
		AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
		AudioPlayer player = playerManager.createPlayer();

		// Create an audio source and add it to the audio connection's queue
		AudioSource source = new LavaplayerAudioSource(api, player);
		audioConnection.setAudioSource(source);

		playerManager.loadItem(
				"https://soundcloud.com/user-487021794/ms-daisy?utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing",
				new AudioLoadResultHandler() {
					@Override
					public void trackLoaded(AudioTrack track) {
						player.playTrack(track);
					}

					@Override
					public void playlistLoaded(AudioPlaylist playlist) {
						for (AudioTrack track : playlist.getTracks()) {
							player.playTrack(track);
						}
					}

					@Override
					public void noMatches() {
						Logger.warn("No playable songs found!");
					}

					@Override
					public void loadFailed(FriendlyException throwable) {
						Logger.error(throwable);
					}
				});
	}
}
