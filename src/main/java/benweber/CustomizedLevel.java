package benweber;

import java.util.ArrayList;
import java.util.Random;

import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.Mario;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.level.Level;
/**
 * A Probabilistic Multi-Pass Level Generator.
 *
 * Ben Weber
 * http://users.soe.ucsc.edu/~bweber
 * August 31, 2010
 * Expressive Intelligence Studio
 * UC Santa Cruz
 *
 * Entry for the CIG 2010 Mario Level Generation Competition. For a detailed explanation:
 *
 * Performs Multiple through the level:
 *  1. Ground
 *  2. Additional hills
 *  3. Pipes
 *  4. Enemies
 *  5. Blocks
 *  6. Coins
 *
 * Constraints are enforced by over-generating and constraining.
 *
 */
public class CustomizedLevel extends Level implements LevelInterface {

	/**
	 * Provides a seed for the level generation, input an integer parameter for static generation.
	 */
	private Random rand = new Random();

	/**
	 * Probabilities of specific events to occur.
	 */
	private static double CHANCE_BLOCK_POWER_UP = 0.1;
	private static double CHANCE_BLOCK_COIN = 0.3;
	private static double CHANCE_BLOCK_ENEMY = 0.2;
	private static double CHANCE_WINGED = 0.5;
	private static double CHANCE_COIN = 0.2;
	private static double CHANCE_PLATFORM = 0.1;
	private static double CHANCE_END_PLATFORM = 0.1;
	private static double CHANCE_ENEMY = 0.1;
	private static double CHANCE_PIPE = 0.1;
	private static double CHANCE_HILL = 0.1;
	private static double CHANCE_END_HILL = 0.3;
	private static double CHANCE_HILL_ENEMY = 0.3;
	private static double CHANCE_GAP = 0.1;
	private static double CHANCE_HILL_CHANGE = 0.1;

	/**
	 * Properties that constrain generation.
	 */
	private static double COIN_HEIGHT = 5;
	private static int PLATFORM_HEIGHT = 4;
	private static int PIPE_MIN_HEIGHT = 2;
	private static double PIPE_HEIGHT = 3.0;
	private static int minX = 5;
	private static double HILL_HEIGHT = 4;
	private static int GAP_LENGTH = 5;
	private static double GAP_OFFSET = -5;
	private static double GAP_RANGE = 10;
	private static int GROUND_MAX_HEIGHT = 5;

	public static double challenge = 0.5;
	public static int levelLength;

	public static void decreaseChallenge(int distance) {
		double complete = ((double)distance)/((double)levelLength);
		challenge -= 0.04*(1 - complete);
		challenge = Math.max(challenge, 0);
		System.out.println("Challenge: " + challenge);
	}

	public static void increaseChallenge(double time) {
		System.out.println(time);

		challenge += (0.05 + 0.0005*time);

		System.out.println("Challenge: " + challenge);
	}

	/**
	 * Counts of instances of objects that must be constrained.
	 */
	int gapCount = 0;
	int turtleCount = 0;
	int coinBlockCount = 0;

	/**
	 * Level constructor specified by the competition.
	 *
	 * @param width - width of the level (tiles)
	 * @param height - height of the level (tiles)
	 * @param seed - ???
	 * @param difficulty - ???
	 * @param type - ???
	 * @param playerMetrics - metrics collected about the player from the training level (ignored by my generator)
	 */
	public CustomizedLevel(int width, int height, long seed, int difficulty, int type, GamePlay playerMetrics) {
		super(width, height);
		levelLength = width;

		Mario.levelString = "" + (int)(challenge*100.0);

		CHANCE_BLOCK_POWER_UP = 0.1 + challenge*challenge*0.0; 	// 0.1
		CHANCE_BLOCK_COIN = 0.3 + -challenge*challenge*0.2;		// 0.3
		CHANCE_BLOCK_ENEMY = 0.0 + challenge*challenge*0.2;		// 0.2
		CHANCE_WINGED = 0.0 + challenge*challenge*0.8;			// 0.8
		CHANCE_COIN = 0.5 + -challenge*challenge*0.3;				// 0.2
		CHANCE_PLATFORM = 0.1 + challenge*challenge*0.1;			// 0.2
		CHANCE_END_PLATFORM = 0.2 + challenge*challenge*0.0;		// 0.2
		CHANCE_ENEMY = 0.02 + challenge*challenge*0.8;			// 0.8
		CHANCE_PIPE = 0.0 + challenge*challenge*0.1;				// 0.1
		CHANCE_HILL = 0.0 + challenge*challenge*0.4;				// 0.4
		CHANCE_END_HILL = 0.0 + challenge*challenge*0.4;			// 0.4
		CHANCE_HILL_ENEMY = 0.0 + challenge*challenge*0.9;		// 0.5
		CHANCE_GAP = 0.01 + challenge*challenge*0.2;				// 0.2
		CHANCE_HILL_CHANGE = 0.05 + challenge*challenge*0.05;		// 0.1
		GAP_LENGTH = 1 + (int)(challenge*challenge*7.0);				// 7
		HILL_HEIGHT = 2 + (int)(challenge*challenge*3.0);				// 4

		// keeps track of the ground height
		ArrayList<Integer> ground = new ArrayList<Integer>();

		// select the starting ground height
		int lastY = GROUND_MAX_HEIGHT + (int)(rand.nextDouble()*(height - 1 - GROUND_MAX_HEIGHT));
		int y = lastY;
		int nextY = y;
		boolean justChanged = false;
		int length = 0;
		int landHeight = height - 1;

		/**
		 * Pass 1: Place the ground
		 */
		for (int x=0; x<width; x++) {

			// need more ground (current gap is too large)
			if (length > GAP_LENGTH && y >= height) {
				nextY = landHeight;
				justChanged = true;
				length = 1;
			}
			// adjust ground level
			else if (x > minX && rand.nextDouble() < CHANCE_HILL_CHANGE && !justChanged) {
				nextY += (int)(GAP_OFFSET + GAP_RANGE*rand.nextDouble());
				nextY = Math.min(height - 2, nextY);
				nextY = Math.max(5, nextY);
				justChanged = true;
				length = 1;
			}
			// add a gap
			// checks that the gap constraint is not violated
			else if (x > minX && y < height &&  rand.nextDouble() < CHANCE_GAP && !justChanged && gapCount < Constraints.gaps) {
				landHeight = Math.min(height - 1, lastY);
				nextY = height;
				justChanged = true;
				length = 1;
				gapCount++;
			}
			// continue placing flat ground
			else {
				length++;
				justChanged = false;
			}

			setGroundHeight(x, y, lastY, nextY);
			ground.add(y);
			lastY = y;
			y = nextY;
		}

		/**
		 * Pass 2: Place additional hills (non-x colliding)
		 */
		int x=0;
		y = height;
		for (Integer h : ground) {	// iterate from left to right at the current ground height
			if (y == height) {

				// start a hill
				if (x > 10 && rand.nextDouble() < CHANCE_HILL) {
					y  = (int)(HILL_HEIGHT + rand.nextDouble()*(h - HILL_HEIGHT));
					setBlock(x, y, Level.HILL_TOP_LEFT);

					for (int i=y + 1; i<h; i++) {
						setBlock(x, i, Level.HILL_LEFT);
					}

				}
			}
			else {
				// end hill if hitting a wall
				if (y >= h) {
					y = height;
				}
				else {
					// end the current hill
					if (rand.nextDouble() < CHANCE_END_HILL) {
						setBlock(x, y, Level.HILL_TOP_RIGHT);

						for (int i=y + 1; i<h; i++) {
							setBlock(x, i, Level.HILL_RIGHT);
						}

						y = height;
					}
					// continue placing the hill
					else {
						setBlock(x, y, Level.HILL_TOP);

						for (int i=y + 1; i<h; i++) {
							setBlock(x, i, Level.HILL_FILL);
						}

						// place enemies on the hill
						if (rand.nextDouble() < CHANCE_HILL_ENEMY) {
							boolean winged = rand.nextDouble() < CHANCE_WINGED;
							int t = (int)(rand.nextDouble()*(SpriteTemplate.CHOMP_FLOWER + 1));

							// check that turtle constraint is not violated
							if (t==SpriteTemplate.GREEN_TURTLE || t==SpriteTemplate.RED_TURTLE) {
								if (turtleCount < Constraints.turtels) {
									turtleCount++;
								}
								else {
									t = SpriteTemplate.GOOMPA;
								}
							}

							setSpriteTemplate(x, y - 1, new SpriteTemplate(t, winged));
						}
					}
				}
			}

			x++;
		}

		/**
		 * Pass 3: Decorate with pipes
		 */
		lastY = 0;
		int lastlastY = 0;
		x=0;
		int lastX = 0;
		for (Integer h : ground) {

			// place a pipe
			if (x > minX && rand.nextDouble() < CHANCE_PIPE) {
				if (h == lastY && lastlastY <= lastY && x > (lastX + 1)) {
					height = PIPE_MIN_HEIGHT + (int)(rand.nextDouble()*PIPE_HEIGHT);
					placePipe(x - 1, h, height);
					lastX = x;
				}
			}

			lastlastY = lastY;
			lastY = h;
			x++;
		}

		/**
		 * Pass 4: Place enemies (on top of the ground, hills and pipes)
		 */
		x=0;
		for (Integer h : ground) {
			if (x > minX && rand.nextDouble() < CHANCE_ENEMY) {
				boolean winged = rand.nextDouble() < CHANCE_WINGED;
				int t = (int)(rand.nextDouble()*(SpriteTemplate.CHOMP_FLOWER + 1));

				// check that the turtle constraint is not violated
				if (t==SpriteTemplate.GREEN_TURTLE || t==SpriteTemplate.RED_TURTLE) {
					if (turtleCount < Constraints.turtels) {
						turtleCount++;
					}
					else {
						t = SpriteTemplate.GOOMPA;
					}
				}

				int tile = getBlock(x, h - 1);
				if (tile == 0) {
					setSpriteTemplate(x, h - 1, new SpriteTemplate(t, winged));
				}

			}

			x++;
		}

		/**
		 * Pass 5: Place blocks
		 */
		x=0;
		y = height;
		for (Integer h : ground) {
			int max = 0;

			// find the highest object
			for (max=0; max<h; max++) {
				int tile = getBlock(x, max);
				if (tile != 0) {
					break;
				}
			}

			if (y == height) {

				// start a block
				if (x > minX && rand.nextDouble() < CHANCE_PLATFORM) {
					y  = max - PLATFORM_HEIGHT; // (int)(-5*rand.nextDouble()*(h - 0));

					if (y >= 1  && h - max > 1) {
						placeBlock(x, y);
					}
					else {
						y = height;
					}
				}
			}
			else {

				// end if hitting a wall
				if (y >= (max + 1)) {
					y = height;
				}
				// end the current block
				else if (rand.nextDouble() < CHANCE_END_PLATFORM) {
					placeBlock(x, y);
					y = height;
				}
				// continue placing the current block
				else {
					placeBlock(x, y);
				}
			}

			x++;
		}

		/**
		 * Pass 6: Decorate with coins
		 */
		x=0;
		for (Integer h : ground) {

			// place a coin
			if (x > 5 && rand.nextDouble() < CHANCE_COIN) {
				y = h - (int)(1 + rand.nextDouble()*COIN_HEIGHT);

				int tile = getBlock(x, y);
				if (tile == 0) {
					setBlock(x, y, Level.COIN);
				}
			}

			x++;
		}

		// place the exit
		this.xExit = width - 5;
	}

	/**
	 * Places a block at the specific level location.
	 */
	public void placeBlock(int x, int y) {

		// choose block type
		if (rand.nextDouble() < CHANCE_BLOCK_POWER_UP) {
			setBlock(x, y, Level.BLOCK_POWERUP);
		}
		else if (rand.nextDouble() < CHANCE_BLOCK_COIN && coinBlockCount < Constraints.coinBlocks) {
			setBlock(x, y, Level.BLOCK_COIN);
			coinBlockCount++;
		}
		else {
			setBlock(x, y, Level.BLOCK_EMPTY);
		}

		// place enemies
		if (rand.nextDouble() < CHANCE_BLOCK_ENEMY) {
			boolean winged = rand.nextDouble() < CHANCE_WINGED;
			int t = (int)(rand.nextDouble()*(SpriteTemplate.CHOMP_FLOWER + 1));

			// turtle constraint
			if (t==SpriteTemplate.GREEN_TURTLE || t==SpriteTemplate.RED_TURTLE) {
				if (turtleCount < Constraints.turtels) {
					turtleCount++;
				}
				else {
					t = SpriteTemplate.GOOMPA;
				}
			}

			setSpriteTemplate(x, y - 1, new SpriteTemplate(t, winged));
		}
	}

	/**
	 * Utility for placing nice-looking pipes.
	 */
	public void placePipe(int x, int y, int height) {
		for (int i=1; i<height; i++) {
			setBlock(x, y - i, Level.TUBE_SIDE_LEFT);
			setBlock(x + 1, y - i, Level.TUBE_SIDE_RIGHT);
		}

		setBlock(x, y - height, Level.TUBE_TOP_LEFT);
		setBlock(x + 1, y - height, Level.TUBE_TOP_RIGHT);
	}

	/**
	 * Utility for placing nice-looking ground.
	 */
	public void setGroundHeight(int x, int y, int lastY, int nextY) {
		for (int i=y + 1; i<height; i++) {
			setBlock(x, i, Level.HILL_FILL);
		}

		if (y < lastY) {
			setBlock(x, y, Level.LEFT_UP_GRASS_EDGE);

			for (int i=y + 1; i<lastY; i++) {
				setBlock(x, i, Level.LEFT_GRASS_EDGE);
			}

			setBlock(x, lastY, Level.RIGHT_POCKET_GRASS);
		}
		else if (y < nextY) {
			setBlock(x, y, Level.RIGHT_UP_GRASS_EDGE);

			for (int i=y + 1; i<nextY; i++) {
				setBlock(x, i, Level.RIGHT_GRASS_EDGE);
			}

			setBlock(x, nextY, Level.LEFT_POCKET_GRASS);
		}
		else {
			setBlock(x, y, Level.HILL_TOP);
		}

		// place the exit
		if (x == (width - 5)) {
			this.yExit = y;
		}
	}
}
