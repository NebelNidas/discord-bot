package softwareschreiber.musicbot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.audio.AudioSourceBase;

public class LavaplayerAudioSource extends AudioSourceBase {
	private final AudioPlayer audioPlayer;
	private AudioFrame lastFrame;

	public LavaplayerAudioSource(DiscordApi api, AudioPlayer audioPlayer) {
		super(api);
		this.audioPlayer = audioPlayer;
	}

	@Override
	public byte[] getNextFrame() {
		if (lastFrame == null) {
			return null;
		}

		return applyTransformers(lastFrame.getData());
	}

	@Override
	public boolean hasFinished() {
		return false;
	}

	@Override
	public boolean hasNextFrame() {
		lastFrame = audioPlayer.provide();
		return lastFrame != null;
	}

	@Override
	public AudioSource copy() {
		return new LavaplayerAudioSource(getApi(), audioPlayer);
	}
}
