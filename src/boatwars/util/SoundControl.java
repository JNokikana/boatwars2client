package boatwars.util;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class SoundControl{
    private AudioInputStream audioStream = null;
    private Clip audioClip = null;
    
    public void playSound(String filename){
        SoundThread soundThread = new SoundThread(filename);
        soundThread.start();
    }
    
    private class MyLineListener implements LineListener {
        @Override
        public void update(LineEvent e) {
            if (e.getType() == LineEvent.Type.STOP) {
                e.getLine().close();
            }
        }
    }
    
    private class SoundThread extends Thread {
        private AudioInputStream audioStream = null;
        private Clip audioClip = null;
        private String filename;
        
        public SoundThread(String filename){
            this.filename = filename;
        }
        
        @Override
        public void run() {
            playSoundclip(filename);
        }

        private void playSoundclip(String filename) {
            String path = "";
            try{
                path = new File("").getCanonicalPath();
            }catch(Exception e){}

            try{
                try{
                    /*System.out.println("Playing sound from: " + path + GameConstants.SOUND_EFFECTS + "sound" + i + ".wav");*/
                    audioStream = AudioSystem.getAudioInputStream(new File(path + File.separator + GameConstants.PATH_SOUND + File.separator + filename));
                }catch(IOException e){
                    System.out.println("Sound file not found!");
                    e.printStackTrace();
                }
                audioClip = AudioSystem.getClip();
                audioClip.open(audioStream);
                audioClip.start();
                audioClip.addLineListener(new MyLineListener());
            }catch(IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }
}
