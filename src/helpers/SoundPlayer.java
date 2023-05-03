package helpers;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static ui.VolumeSlider.actualVolume;

public class SoundPlayer {

	public static Clip playSound(String fileName) {

		try (InputStream is = SoundPlayer.class.getResourceAsStream(fileName)) {
			if (is == null) {
				System.err.println("Error: Sound file not found\n" + fileName + "\n");
				return null;
			}

			BufferedInputStream bis = new BufferedInputStream(is);
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(bis));

			// min: -80
			// max: 6
			FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue(actualVolume);

			// Start the clip
			clip.start();
			return clip;
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Clip playSoundLoop(String fileName) {
		try (InputStream is = SoundPlayer.class.getResourceAsStream(fileName)) {
			if (is == null) {
				System.err.println("Error: Sound file not found\n" + fileName + "\n");
				return null;
			}

			BufferedInputStream bis = new BufferedInputStream(is);
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(bis));

			// Set the clip to loop continuously
			clip.loop(Clip.LOOP_CONTINUOUSLY);

			FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue(actualVolume);

			// Start the clip
			clip.start();
			return clip;
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		return null;
	}
}
