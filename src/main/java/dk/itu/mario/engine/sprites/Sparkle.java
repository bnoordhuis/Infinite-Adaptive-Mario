package dk.itu.mario.engine.sprites;

import dk.itu.mario.engine.Art;


public class Sparkle extends Sprite
{
    public int life;
    public int xPicStart;

    public Sparkle(int x, int y, float xa, float ya)
    {
        this(x, y, xa, ya, (int)(Math.random()*2), 0, 5);
    }

    public Sparkle(int x, int y, float xa, float ya, int xPic, int yPic, int timeSpan)
    {
        sheet = Art.particles;
        this.x = x;
        this.y = y;
        this.xa = xa;
        this.ya = ya;
        this.xPic = xPic;
        xPicStart = xPic;
        this.yPic = yPic;
        this.xPicO = 4;
        this.yPicO = 4;

        wPic = 8;
        hPic = 8;
        life = 10+(int)(Math.random()*timeSpan);
    }

    public void move()
    {
        if (life>10)
            xPic = 7;
        else
            xPic = xPicStart+(10-life)*4/10;

        if (life--<0) Sprite.spriteContext.removeSprite(this);

        x+=xa;
        y+=ya;
    }
}
