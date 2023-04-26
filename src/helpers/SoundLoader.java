package helpers;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundLoader {

	public static Clip playAudio(String fileName, double... volumeArr) {
		float volume = (float) (volumeArr.length > 0 ? volumeArr[0] : 1.0); // set default volume of 1.0 if volume argument is not provided
		try (InputStream is = new BufferedInputStream(SoundLoader.class.getResourceAsStream(fileName))) {

			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(is));

			// if volume is given, use the control to set a new volume between -10 and 0 (good hearing range)
			if (volumeArr.length > 0) {
				FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				float newVolume = (volume * 10 - 10) * 2;
				control.setValue(newVolume);
			}

			// Start the clip
			clip.start();
			return clip;
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Clip playAudioLoop(String fileName, double... volumeArr) {
		float volume = (float) (volumeArr.length > 0 ? volumeArr[0] : 1.0); // set default volume of 1.0 if volume argument is not provided
		try (InputStream is = new BufferedInputStream(SoundLoader.class.getResourceAsStream(fileName))) {

			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(is));

			// if volume is given, use the control to set a new volume between -10 and 0 (good hearing range)
			if (volumeArr.length > 0) {
				FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				float newVolume = (volume * 10 - 10) * 2;
				control.setValue(newVolume);
			}

			// Set the clip to loop continuously
			clip.loop(Clip.LOOP_CONTINUOUSLY);

			// Start the clip
			clip.start();
			return clip;
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		return null;
	}


}
