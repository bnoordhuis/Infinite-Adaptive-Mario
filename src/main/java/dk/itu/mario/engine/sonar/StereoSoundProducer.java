package dk.itu.mario.engine.sonar;

public interface StereoSoundProducer
{
    public float read(float[] leftBuf, float[] rightBuf, int readRate);
    public void skip(int samplesToSkip, int readRate);
}