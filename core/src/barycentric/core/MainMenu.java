package barycentric.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenu
{

    FitViewport viewport;

    Texture titleTex;

    float updateTimer = 1.f;
    Array<Rectangle> backgroundRects = new Array();

    Texture play = new Texture("play.png");
    Texture playH = new Texture("playHovered.png");


    boolean started = false;

    MainMenu()
    {
        viewport = new FitViewport(Constants.WIDTH, Constants.HEIGHT);
        viewport.getCamera().position.x = Constants.WIDTH / 2.f;
        viewport.getCamera().position.y = Constants.HEIGHT / 2.f;
        viewport.getCamera().update();

        titleTex = new Texture("title.png");

        for(int i = 0; i < 24; i++) backgroundRects.add(new Rectangle());

        float bgWidth = 1.f/24.f;
        for(int i = 0; i < backgroundRects.size; i ++)
        {
            float x = -0.98f + 2 * i * bgWidth;

            float bgHeight = MathUtils.random(0.2f, 1.25f);

            backgroundRects.get(i).set(x, -1.f, bgWidth, bgHeight);
        }
    }

    void reset()
    {
        started = false;
    }


    boolean gameStarted()
    {
        return started;
    }

    void update(float dt)
    {
        updateTimer -= dt;
        if(updateTimer <= 0)
        {
            updateTimer = 1;
            float bgWidth = 1.f/24.f;
            for(int i = 0; i < backgroundRects.size; i ++)
            {
                float x = -0.98f + 2 * i * bgWidth;

                float bgHeight = MathUtils.random(0.2f, 1.25f);

                backgroundRects.get(i).set(x, -1.f, bgWidth, bgHeight);
            }
        }
    }

    void render(ShapeRenderer sh, SpriteBatch s)
    {
        Color barColor = Color.GREEN.cpy().mul(0.7f);
        barColor.a = 1.f;

        sh.setProjectionMatrix(new Matrix4().idt());
        sh.begin(ShapeRenderer.ShapeType.Filled);
        sh.setColor(barColor);

        for(Rectangle r : backgroundRects)
        {
            sh.rect(r.x, r.y, r.width, r.height);
        }

        sh.end();

        float ratio = (float)titleTex.getWidth() / (float)titleTex.getHeight() * 0.25f;
        s.begin();
        s.setProjectionMatrix(new Matrix4().idt());
        s.draw(titleTex, -ratio / 2, 0f, ratio, 1.f);

        Texture t = play;
        float mx = Gdx.input.getX();
        mx /= (float)Gdx.graphics.getWidth();
        mx *= 2;
        mx -= 1;

        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        my /= (float)Gdx.graphics.getHeight();
        my *= 2;
        my -= 1;

        if(mx > -0.25 && mx < 0.25f)
        {
            if(my > -0.5f && my < 0)
            {
                t = playH;

                if(Gdx.input.isTouched())
                {
                    started = true;
                }
            }
        }

        s.draw(t, -0.25f, -0.5f, 0.5f, 0.5f);

        s.end();


    }

    void resize(int w, int h)
    {
        viewport.update(w, h);
        viewport.apply();
    }
}
