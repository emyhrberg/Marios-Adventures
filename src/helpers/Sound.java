package helpers;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static ui.VolumeSlider.actualVolume;

public class Sound {

	public static Clip play(String fileName) {
		try (InputStream is = Sound.class.getResourceAsStream(fileName)) {
			if (is == null) {
				System.err.println("Error: Sound file not found\n" + fileName + "\n");
				return null; // handle sound not found
			}

			// wrap in buffered stream
			BufferedInputStream bis = new BufferedInputStream(is); // wrap in buffered stream
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(bis));

			// control values for sound is minimum: -80 and maximum: 6
			FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue(actualVolume);

			clip.start();
			return clip; // return the clip started
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Clip playSoundLoop(String fileName) {
		try (InputStream is = Sound.class.getResourceAsStream(fileName)) {

			// handle sound not found
			if (is == null) {
				System.err.println("Error: Sound file not found\n" + fileName + "\n");
				return null;
			}

			// wrap in buffered stream
			BufferedInputStream bis = new BufferedInputStream(is);
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(bis));

			// play sound in infinite loop
			clip.loop(Clip.LOOP_CONTINUOUSLY);

			// control values for sound is minimum: -80 and maximum: 6
			FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue(actualVolume);

			clip.start();
			return clip; // return the clip started
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
			return null;
		}
	}
}
