package barycentric.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;

public class Stage extends InputAdapter
{
    boolean stageComplete = false;

    TextureAtlas comboAtlas;
    float animationTimer = 0 ;
    Animation<TextureRegion> comboAnimation;

    int combo = 0;
    float comboScale = 1.f;

    ParticleSystem ps;
    Music music;

    Array<Rectangle> noteBlocks = new Array();
    Array<Boolean>   rotatePositive = new Array();
    Array<Color> colors = new Array();

    int currentIndex = 0;

    boolean particleSpawned = false;
    boolean rectHasCollided = false;

    float hue = 0;
    Color bgCol = new Color();

    Array<Circle> backgroundCircles = new Array();
    Array<Rectangle> backgroundRects = new Array();

    PlayerPair pair;

    float circleAlpha = 0;

    void reset()
    {
        animationTimer = 0;
        currentIndex = 0;
        circleAlpha = 0;
        rectHasCollided = false;
        particleSpawned = false;
        combo = 0;
        comboScale = 1.f;
        stageComplete = false;
        randomizeBGRects(0);
        randomizeBGCircles();
        music.stop();
        music.setOnCompletionListener(new Music.OnCompletionListener()
        {
            @Override
            public void onCompletion(Music music)
            {
                stageComplete = true;
            }
        });
    }

    public boolean keyDown (int keycode)
    {
        if(keycode != Input.Keys.SPACE) return false;
        if(pair == null) return false;

        Rectangle rect = noteBlocks.get(currentIndex);

        float distanceFromCenter = Vector2.dst(pair.getRotatingCirclePos().x,
                pair.getRotatingCirclePos().y,
                rect.x + rect.width / 2,
                rect.y + rect.height / 2);

        boolean hit = false;
        if(distanceFromCenter < pair.getRadius() * 0.35f)
        {
            hit = true;
            ps.addParticle(ParticleSystem.Type.Perfect);
            combo++;
        }
        else if(distanceFromCenter < pair.getRadius() * 0.65f)
        {
            hit = true;
            ps.addParticle(ParticleSystem.Type.Great);
            combo++;
        }
        else if(distanceFromCenter < pair.getRadius() * 0.85f)
        {
            hit = true;
            ps.addParticle(ParticleSystem.Type.Good);
        }

        if(hit)
        {
            comboScale = 2.5f;

            hue += 30.f * 1.61803398875;

            randomizeColors();
            rectHasCollided = false;
            particleSpawned = false;

            pair.getRotatingCirclePos().x = rect.x + rect.width / 2;
            pair.getRotatingCirclePos().y = rect.y + rect.height / 2;

            currentIndex++;

            pair.setNextCircle();

            if(!music.isPlaying())
            {
                music.play();
            }

            randomizeBGRects(getComboLevel());
        }
        else
        {
            combo = 0;
            music.pause();
        }
        return false;
    }

    public boolean touchDown (int screenX, int screenY, int pointer, int button)
    {
        if(pair == null) return false;

        Rectangle rect = noteBlocks.get(currentIndex);

        float distanceFromCenter = Vector2.dst(pair.getRotatingCirclePos().x,
                pair.getRotatingCirclePos().y,
                rect.x + rect.width / 2,
                rect.y + rect.height / 2);

        boolean hit = false;
        if(distanceFromCenter < pair.getRadius() * 0.35f)
        {
            hit = true;
            ps.addParticle(ParticleSystem.Type.Perfect);
            combo++;
        }
        else if(distanceFromCenter < pair.getRadius() * 0.65f)
        {
            hit = true;
            ps.addParticle(ParticleSystem.Type.Great);
            combo++;
        }
        else if(distanceFromCenter < pair.getRadius() * 0.85f)
        {
            hit = true;
            ps.addParticle(ParticleSystem.Type.Good);
        }

        if(hit)
        {
            comboScale = 2.5f;

            hue += 30.f * 1.61803398875;

            randomizeColors();
            rectHasCollided = false;
            particleSpawned = false;

            pair.getRotatingCirclePos().x = rect.x + rect.width / 2;
            pair.getRotatingCirclePos().y = rect.y + rect.height / 2;

            currentIndex++;

            pair.setNextCircle();

            if(!music.isPlaying())
            {
                music.play();
            }

            randomizeBGRects(getComboLevel());
        }
        else
        {
            combo = 0;
            music.pause();
        }
        return false;
    }

    void randomizeBGCircles()
    {
        backgroundCircles.clear();

        for(int i = 0; i < 12; i++)
        {
            float x = MathUtils.random(-0.8f, 0.8f);
            float y = MathUtils.random(-0.7f, 1.2f);
            float r = MathUtils.random(0.1f, 0.3f);
            backgroundCircles.add(new Circle(x, y, r));
        }
    }


    Stage(ParticleSystem ps)
    {
        this.ps = ps;

        comboAtlas = new TextureAtlas("combo/textureAtlas.atlas");
        Array<TextureRegion> frames = new Array();
        for(TextureRegion t : comboAtlas.getRegions())
        {
            frames.add(t);
        }
        comboAnimation = new Animation(1.f/32.f, frames, Animation.PlayMode.LOOP);

        music = Gdx.audio.newMusic(Gdx.files.internal("music2.mp3"));

        music.play();
        music.pause();



        float size = 64;

        TiledMap map = new TmxMapLoader().load("stage/stageLayout2.tmx");
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("collision");
        int width = map.getProperties().get("width", Integer.class);
        int height = map.getProperties().get("height", Integer.class);


        int cellX = 0;
        int cellY = 0;


        boolean dir =  false;
        //last cell in series will be null, this is how we check for the end of a stage
        while(layer.getCell(cellX, cellY) != null)
        {
            Rectangle rect = new Rectangle(cellX * size, cellY * size, size, size);
            noteBlocks.add(rect);
            colors.add(new Color());

            if(layer.getCell(cellX, cellY).getTile().getProperties().containsKey("left"))
            {
                boolean topFree = true;
                boolean bottomFree = true;

                //top
                if(layer.getCell(cellX, cellY + 1) != null ||
                   layer.getCell(cellX + 1, cellY + 1) != null ||
                   layer.getCell(cellX - 1, cellY + 1) != null)
                {
                    topFree = false;
                }
                //bottom
                if(layer.getCell(cellX, cellY - 1) != null ||
                   layer.getCell(cellX + 1, cellY - 1) != null ||
                   layer.getCell(cellX - 1, cellY - 1) != null)
                {
                    bottomFree = false;
                }

                //////////////////////////////////
                if(!bottomFree && !topFree)
                {
                    System.out.println(cellX + " : " +  cellY);
                    System.out.flush();
                    throw new IllegalStateException();
                }
                else if(topFree && bottomFree)
                {
                    rotatePositive.add(dir);
                    dir = !dir;
                }
                else if(topFree)
                {
                    rotatePositive.add(true);
                }
                else if(bottomFree)
                {
                    rotatePositive.add(false);
                }
                cellX--;
            }
            else if(layer.getCell(cellX, cellY).getTile().getProperties().containsKey("right"))
            {
                boolean topFree = true;
                boolean bottomFree = true;

                //top
                if(layer.getCell(cellX, cellY + 1) != null ||
                   layer.getCell(cellX + 1, cellY + 1) != null ||
                   layer.getCell(cellX - 1, cellY + 1) != null)
                {
                    topFree = false;
                }
                //bottom
                if(layer.getCell(cellX, cellY - 1) != null ||
                        layer.getCell(cellX + 1, cellY - 1) != null ||
                        layer.getCell(cellX - 1, cellY - 1) != null)
                {
                    bottomFree = false;
                }

                //////////////////////////////////
                if(!bottomFree && !topFree)
                {
                    System.out.println(cellX + " : " +  cellY);
                    System.out.flush();
                    throw new IllegalStateException();
                }
                else if(bottomFree && topFree)
                {
                    rotatePositive.add(dir);
                    dir = !dir;
                }
                else if(topFree)
                {
                    rotatePositive.add(false);
                }
                else if(bottomFree)
                {
                    rotatePositive.add(true);
                }

                cellX++;
            }
            else if(layer.getCell(cellX, cellY).getTile().getProperties().containsKey("down"))
            {
                boolean leftFree = true;
                boolean rightFree = true;


                //right
                if(layer.getCell(cellX + 1, cellY) != null ||
                   layer.getCell(cellX + 1, cellY - 1) != null ||
                   layer.getCell(cellX + 1, cellY + 1) != null)
                {
                    rightFree = false;
                }
                //left
                if(layer.getCell(cellX - 1, cellY) != null ||
                   layer.getCell(cellX - 1, cellY - 1) != null ||
                   layer.getCell(cellX - 1, cellY + 1) != null)
                {
                    leftFree = false;
                }




                //////////////////////////////////
                if(!leftFree && !rightFree)
                {
                    System.out.println(cellX + " : " +  cellY);
                    System.out.flush();
                    throw new IllegalStateException();
                }
                else if(leftFree && rightFree)
                {
                    rotatePositive.add(dir);
                    dir = !dir;
                }
                else if(leftFree)
                {
                    rotatePositive.add(true);
                }
                else if(rightFree)
                {
                    rotatePositive.add(false);
                }
                cellY--;
            }
            else if(layer.getCell(cellX, cellY).getTile().getProperties().containsKey("up"))
            {
                boolean leftFree = true;
                boolean rightFree = true;


                //right
                if(layer.getCell(cellX + 1, cellY) != null ||
                        layer.getCell(cellX + 1, cellY - 1) != null ||
                        layer.getCell(cellX + 1, cellY + 1) != null)
                {
                    rightFree = false;
                }
                //left
                if(layer.getCell(cellX - 1, cellY) != null ||
                        layer.getCell(cellX - 1, cellY - 1) != null ||
                        layer.getCell(cellX - 1, cellY + 1) != null)
                {
                    leftFree = false;
                }

                //////////////////////////////////
                if(!leftFree && !rightFree)
                {
                    System.out.println(cellX + " : " +  cellY);
                    System.out.flush();
                    throw new IllegalStateException();
                }
                else if(leftFree && rightFree)
                {
                    rotatePositive.add(dir);
                    dir = !dir;
                }
                else if(leftFree)
                {
                    rotatePositive.add(false);
                }
                else if(rightFree)
                {
                    rotatePositive.add(true);
                }

                cellY++;
            }
        }

        map.dispose();
        randomizeColors();
        music.setOnCompletionListener(new Music.OnCompletionListener()
        {
            @Override
            public void onCompletion(Music music)
            {
                stageComplete = true;
            }
        });


        for(int i = 0; i < 24; i++) backgroundRects.add(new Rectangle());
        randomizeBGRects(0);
        randomizeBGCircles();
    }

    void randomizeBGRects(int level)
    {
        float maxHeight = 0.5f;
        if(level == 1)
        {
            maxHeight = 0.8f;
        }
        if(level == 2)
        {
            maxHeight = 1.2f;
        }
        if(level == 3)
        {
            maxHeight = 1.5f;
        }

        if(level == 4)
        {
            maxHeight = 1.8f;
        }

        float bgWidth = 1.f/24.f;
        for(int i = 0; i < backgroundRects.size; i ++)
        {
            float x = -0.98f + 2 * i * bgWidth;

            float bgHeight = MathUtils.random(0.2f, maxHeight);

            backgroundRects.get(i).set(x, -1.f, bgWidth, bgHeight);
        }
    }


    int getComboLevel()
    {
        if(combo >= 32) return 4;

        if(combo >= 16)  return 3;

        if(combo >= 16) return 2;

        if(combo >= 8) return 1;

        return 0;
    }

    void randomizeColors()
    {
        for(Color color : colors)
        {
            float rand = MathUtils.random();

            if (rand > 0.4f)
            {
                color.set(Color.MAROON);
            }
            else if(rand > 0.2f)
            {
                color.set(Color.CORAL);
            }
            else
            {
                color.set(Color.FIREBRICK);
            }
        }
    }

    boolean isStageComplete()
    {
        return stageComplete;
    }


    void update(float dt, PlayerPair pair)
    {
        if(currentIndex == noteBlocks.size - 1)
        {
            stageComplete = true;
            music.stop();
        }
        if(stageComplete)
        {
            return;
        }
        this.pair =  pair;
        animationTimer += dt;


        pair.setRotateDir(rotatePositive.get(currentIndex));

        Rectangle rect = noteBlocks.get(currentIndex);


        Circle c = new Circle(pair.getRotatingCirclePos(), pair.getRadius());

        if(Intersector.overlaps(c, rect))
        {
            rectHasCollided = true;
        }
        else
        {
            if(rectHasCollided)
            {
                combo = 0;
                music.pause();

                if(!particleSpawned)
                {
                    ps.addParticle(ParticleSystem.Type.Miss);
                    particleSpawned = true;
                }
            }
        }

        for(Circle circle : backgroundCircles)
        {
            circle.y -= circle.radius * dt / 60.f;
        }
        for(Rectangle r : backgroundRects)
        {
            r.height = MathUtils.lerp(r.height, 0.2f, dt * 2.f);
        }

        comboScale = MathUtils.lerp(comboScale, 1.f, dt * 2.f);

        circleAlpha = MathUtils.lerp(circleAlpha, getComboLevel() / 6.f, dt * 0.25f);
    }

    void render(SpriteBatch s)
    {
        if(combo >= 4)
        {
            TextureRegion t = comboAnimation.getKeyFrame(animationTimer);

            float rotation = 15.f * MathUtils.sin(animationTimer);
            s.draw(t, -1.25f, 0.38f, 0.5f, 0.5f,  1, 1, comboScale * 0.25f, comboScale * 0.25f, rotation);
        }
    }

    void render(ShapeRenderer sh)
    {
        Matrix4 proj = sh.getProjectionMatrix().cpy();
        sh.end();

        sh.setProjectionMatrix(new Matrix4().idt());
        sh.begin(ShapeRenderer.ShapeType.Filled);

        sh.setColor(bgCol.fromHsv(hue, 1.f, (float)getComboLevel() / 4.f));
        sh.rect(-1.f, -1.f, 2.f, 2.f);


        sh.setColor(0.4f, 0.7f, 0.7f, circleAlpha);
        for(Circle circle : backgroundCircles)
        {
            sh.circle(circle.x, circle.y, circle.radius);
        }


        Color barColor = Color.GREEN.cpy().mul(0.7f);
        barColor.a = 1.f;
        sh.setColor(barColor);

        for(Rectangle r : backgroundRects)
        {
            sh.rect(r.x, r.y, r.width, r.height);
        }

        sh.end();
        sh.begin(ShapeRenderer.ShapeType.Filled);

        sh.setProjectionMatrix(proj);
        for(int i = 0; i < noteBlocks.size; i++)
        {
            Rectangle r = noteBlocks.get(i);
            Color c = colors.get(i);

            sh.setColor(c);

            sh.rect(r.x + 2, r.y + 2, r.width - 2, r.height - 2);
        }

        sh.setColor(Color.WHITE);
        Rectangle draw = noteBlocks.get(currentIndex);
        sh.rect(draw.x, draw.y, draw.width, draw.height);
    }
}
