package uk.ac.reading.sis05kol.mooc;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.Random;

/**
 * Created by Jez on 28/12/2016.
 */

public class SoundPlayer {

    private static SoundPool sp;
    private static int hillarySound;
    private static int hitSound;
    private static int loseSound;
    final int[] trumpSoundArray = new int[4];
    final Random r = new Random();

    public SoundPlayer(Context context) {

        // SoundPool constructor:
        // SoundPool(int maxStreams, int streamType, int srcQuality)
        sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        hillarySound = sp.load(context, R.raw.hillary1, 1);
        hitSound = sp.load(context, R.raw.hit, 1);
        loseSound = sp.load(context, R.raw.lose, 1);
        // lose - We will make america great again

        trumpSoundArray[0] = sp.load(context, R.raw.trump1, 1);
        trumpSoundArray[1] = sp.load(context, R.raw.trump2, 1);
        trumpSoundArray[2] = sp.load(context, R.raw.trump3, 1);
        trumpSoundArray[3] = sp.load(context, R.raw.trump4, 1);
        // Trump Sounds:
        // trump1 - I don't care... I don't care
        // trump2 - I'm really rich
        // trump3 - Crooked Hillary
        // trump4 - I love China
    }

    public void playHillarySound(){
        // play(int soundID,float leftVolume,float rightVolume,int priority,int loop,float rate)
        sp.play(hillarySound, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void playTrumpSound(){
        // play(int soundID,float leftVolume,float rightVolume,int priority,int loop,float rate)
        sp.play(trumpSoundArray[r.nextInt(4)],1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitSound(){
        // play(int soundID,float leftVolume,float rightVolume,int priority,int loop,float rate)
        sp.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playLoseSound(){

        // play(int soundID,float leftVolume,float rightVolume,int priority,int loop,float rate)
        sp.play(loseSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
