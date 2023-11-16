//package helpers;
//
//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.player.Player;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//public class OldSound {
//    private Player player;
//    private Thread playbackThread;
//
//    public void play(String fileToPlay) {
//
//        playbackThread = new Thread(() -> {
//            try (InputStream is = OldSound.class.getResourceAsStream(fileToPlay)) {
//                player = new Player(is);
//                player.play();
//            } catch (JavaLayerException | IOException e) {
//                e.printStackTrace();
//            }
//        });
//        playbackThread.start();
//    }
//
//    public void stop() {
//        if (player != null) {
//            player.close();
//        }
//    }
//}
