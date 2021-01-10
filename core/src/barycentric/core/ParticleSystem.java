package barycentric.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ParticleSystem
{
    FitViewport viewport;
    SpriteBatch s;

    enum Type
    {
        Miss,
        Good,
        Great,
        Perfect,
    }

    Texture missTex;
    Texture goodTex;
    Texture greatTex;
    Texture perfTex;

    class Particle
    {
        float x;
        float y;
        float w;
        float h;


        Texture tex;
    }

    Pool<Particle> particlePool;
    Array<Particle> particles;

    ParticleSystem()
    {
        particlePool = new Pool<Particle>(16)
        {
            @Override
            protected Particle newObject()
            {
                return new Particle();
            }
        };

        viewport = new FitViewport(Constants.WIDTH, Constants.HEIGHT);
        viewport.getCamera().position.x = Constants.WIDTH / 2.f;
        viewport.getCamera().position.y = Constants.HEIGHT / 2.f;
        viewport.getCamera().update();
        s = new SpriteBatch();

        particles = new Array();

        missTex = new Texture("miss.png");
        goodTex = new Texture("good.png");
        greatTex = new Texture("great.png");
        perfTex = new Texture("perfect.png");
    }

    void addParticle(Type type)
    {
        Particle p = particlePool.obtain();

        if(type == Type.Miss)
        {
            p.tex = missTex;
            p.w = 256;
        }
        if(type == Type.Good)
        {
            p.tex = goodTex;
            p.w = 256;
        }
        if(type == Type.Great)
        {
            p.tex = greatTex;
            p.w = 256;
        }
        if(type == Type.Perfect)
        {
            p.tex = perfTex;
            p.w = 384;
        }

        p.x = 32;
        p.y = Constants.HEIGHT - 128;
        float ratio = (float)p.tex.getHeight() / (float)p.tex.getWidth();

        p.h = ratio * p.w;

        particles.add(p);
    }

    void reset()
    {
        particlePool.freeAll(particles);
        particles.clear();
    }


    void update(float dt)
    {
        Array<Particle> toRemove = new Array();
        for (Particle p : particles)
        {
            p.y -= Constants.HEIGHT * dt;

            if(p.y + p.h < 0)
            {
                toRemove.add(p);
            }
        }
        particles.removeAll(toRemove, true);
        particlePool.freeAll(toRemove);
    }

    void render()
    {
        s.begin();
        s.setProjectionMatrix(viewport.getCamera().combined);

        for (Particle p : particles)
        {
            s.draw(p.tex, p.x, p.y, p.w, p.h);
        }

        s.end();
    }
    void resize(int w, int h)
    {
        viewport.update(w, h);
        viewport.apply();
    }


    void dispose()
    {
        s.dispose();
    }

}
