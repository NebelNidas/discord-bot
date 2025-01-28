package softwareschreiber.musicbot;

import java.util.Locale;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.tinylog.Logger;

class VoiceChatJoinCommand implements MessageCreateListener {
	private final DiscordApi api;
	private ServerVoiceChannel serverVoiceChannel;
	private static final String playCommand = "!play";
	private static final String thorsten = "thorsten";
	private static final String[] thorstenMessages = new String[] {
			"Hab ich da meinen Namen gehört?",
			"Der einzig wahre Magga himself",
			"Willkommen zum besten Fach der Welt",
			"Spaß bei Seite jetzt!",
			"Ich werd euch so richtig durchnehmen",
			"Wurden Sie nicht besser erzogen?",
			"Für Bonuspunkte, bitte nach der Stunde zu mir...",
			"Habt ihr mich vermisst?"
	};

	VoiceChatJoinCommand(DiscordApi api) {
		this.api = api;
	}

	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		String messageContent = event.getMessageContent();

		if (messageContent.startsWith(playCommand)) {
			if (messageContent.length() <= playCommand.length() + 1) {
				event.getChannel().sendMessage("!play muss mit einem Link zusammen geschickt werden");
				return;
			}

			serverVoiceChannel = event.getMessageAuthor().getConnectedVoiceChannel().get();
			String url = event.getMessageContent().substring(playCommand.length() + 1);
			serverVoiceChannel.connect().thenAccept(audioConnection -> playAudio(audioConnection, url));
		} else if (event.getMessageContent().equalsIgnoreCase("!stop")) {
			if (serverVoiceChannel != null) {
				serverVoiceChannel.disconnect().join();
				serverVoiceChannel = null;
			} else {
				event.getChannel().sendMessage("Kann nichts stoppen was schon gestoppt ist");
			}
		} else if (messageContent.toLowerCase(Locale.ROOT).contains(thorsten)) {
			event.getChannel()
					.sendMessage(thorstenMessages[(int) Math.round(Math.random() * (thorstenMessages.length - 1))]);
			return;
		}
	}

	private void playAudio(AudioConnection audioConnection, String link) {
		AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
		playerManager.registerSourceManager(new YoutubeAudioSourceManager());
		playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
		playerManager.registerSourceManager(new BandcampAudioSourceManager());

		AudioPlayer player = playerManager.createPlayer();

		// Create an audio source and add it to the audio connection's queue
		AudioSource source = new LavaplayerAudioSource(api, player);
		audioConnection.setAudioSource(source);

		playerManager.loadItem(link, new AudioLoadResultHandler() {
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
