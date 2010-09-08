package dk.itu.mario.level.generator;

import java.util.Random;

import benweber.CustomizedLevel;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;

public class CustomizedLevelGenerator implements LevelGenerator{

	public LevelInterface generateLevel(GamePlay playerMetrics) {
		LevelInterface level = new CustomizedLevel(160,15,new Random().nextLong(),1,1,playerMetrics);
		return level;
	}

}
