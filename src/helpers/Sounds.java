package helpers;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.IOException;
import java.io.InputStream;

public class Sounds {
    private Player player;
    private Thread playbackThread;

    public void play(String fileName) {

        playbackThread = new Thread(() -> {
            try (InputStream is = Sounds.class.getResourceAsStream(fileName)) {
                player = new Player(is);
                player.play();
            } catch (IOException | JavaLayerException e) {
                e.printStackTrace();
            }
        });
        playbackThread.start();
    }

    public void stop() {
        if (player != null) {
            player.close();
        }
    }
}
