package dk.itu.mario.engine.sprites;

import dk.itu.mario.engine.Art;
import dk.itu.mario.level.Level;



public class CoinAnim extends Sprite
{
    private int life = 10;

    public CoinAnim(int xTile, int yTile)
    {
        sheet = Art.level;
        wPic = hPic = 16;

        x = xTile * 16;
        y = yTile * 16 - 16;
        xa = 0;
        ya = -6f;
        xPic = 0;
        yPic = 2;
    }

    public void move()
    {
        if (life-- < 0)
        {
            Sprite.spriteContext.removeSprite(this);
            for (int xx = 0; xx < 2; xx++)
                for (int yy = 0; yy < 2; yy++)
                    Sprite.spriteContext.addSprite(new Sparkle((int)x + xx * 8 + (int) (Math.random() * 8), (int)y + yy * 8 + (int) (Math.random() * 8), 0, 0, 0, 2, 5));
        }

        xPic = life & 3;

        x += xa;
        y += ya;
        ya += 1;
    }
}
