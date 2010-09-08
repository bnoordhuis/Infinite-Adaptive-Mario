package dk.itu.mario.engine.sonar;



import dk.itu.mario.engine.sonar.sample.SonarSample;


public class FakeSoundEngine extends SonarSoundEngine
{
	@Override
	public void setListener(SoundListener soundListener)
	{
	}

	@Override
	public void shutDown()
	{
	}

	@Override
	public SonarSample loadSample(String resourceName)
	{
		return null;
	}

	@Override
	public void play(SonarSample sample, SoundSource soundSource, float volume, float priority, float rate)
	{
	}

	@Override
	public void clientTick(float alpha)
	{
	}

	@Override
	public void tick()
	{
	}

	@Override
	public void run()
	{
	}
}
