package dk.itu.mario.engine.sonar.sample;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.*;
import dk.itu.mario.engine.sonar.sample.SonarSample;
import dk.itu.mario.res.ResourcesManager;


public class SampleLoader
{
    /**
     * Loads a sample from an url
     */
    public static SonarSample loadSample(String resourceName) throws UnsupportedAudioFileException, IOException
    {
        // Hack to prevent "mark/reset not supported" on some systems
        InputStream in=ResourcesManager.class.getResourceAsStream(resourceName);
        byte[] d = rip(in);
        AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(d));
        return buildSample(rip(ais), ais.getFormat());
    }

    /**
     * Rips the entire contents of an inputstream into a byte array
     */
    private static byte[] rip(InputStream in) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[4096];

        int read = 0;
        while ((read = in.read(b)) > 0)
        {
            bos.write(b, 0, read);
        }

        bos.close();
        return bos.toByteArray();
    }

    /**
     * Reorganizes audio sample data into the intenal sonar format
     */
    private static SonarSample buildSample(byte[] b, AudioFormat af) throws UnsupportedAudioFileException
    {
        // Rip audioformat data
        int channels = af.getChannels();
        int sampleSize = af.getSampleSizeInBits();
        float rate = af.getFrameRate();
        boolean signed = af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;

        // Sanity checking
        if (channels != 1) throw new UnsupportedAudioFileException("Only mono samples are supported");
        if (!(sampleSize == 8 || sampleSize == 16 || sampleSize == 32)) throw new UnsupportedAudioFileException("Unsupported sample size");
        if (!(af.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED || af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED)) throw new UnsupportedAudioFileException("Unsupported encoding");

        // Wrap the data into a bytebuffer, and set up the byte order
        ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(af.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

        int s = b.length / (sampleSize / 8);
        float[] buf = new float[s];
        // Six different cases for reordering the data. Can this be improved without slowing it down?
        if (sampleSize == 8)
        {
            if (signed)
            {
                for (int i = 0; i < s; i++)
                    buf[i] = bb.get() / (float)0x80;
            }
            else
            {
                for (int i = 0; i < s; i++)
                    buf[i] = ((bb.get()&0xFF)-0x80) / (float)0x80;
            }
        }
        else if (sampleSize == 16)
        {
            if (signed)
            {
                for (int i = 0; i < s; i++)
                    buf[i] = bb.getShort() / (float)0x8000;
            }
            else
            {
                for (int i = 0; i < s; i++)
                    buf[i] = ((bb.getShort()&0xFFFF)-0x8000) / (float)0x8000;
            }
        }
        else if (sampleSize == 32)
        {
            if (signed)
            {
                for (int i = 0; i < s; i++)
                    buf[i] = bb.getInt() / (float)0x80000000;
            }
            else
            {
                // Nasty.. check this.
                for (int i = 0; i < s; i++)
                    buf[i] = ((bb.getInt()&0xFFFFFFFFl)-0x80000000l) / (float)0x80000000;
            }
        }

        // Return the completed sample
        return new SonarSample(buf, rate);
    }
}
