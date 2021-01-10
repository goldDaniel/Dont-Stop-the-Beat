package barycentric.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PlayerPair
{
    private Vector2 circlePos0 = new Vector2(32, -32);
    private Vector2 circlePos1 = new Vector2(32, -96);

    private Vector2 currentCircle = circlePos0;

    private float radius = 32;

    boolean rotatePositive = false;

    float accumulatorAngle = -MathUtils.HALF_PI;


    void update(float dt)
    {

        if(rotatePositive)
        {
            accumulatorAngle += MathUtils.PI * rotationTime() * dt;
        }
        else
        {
            accumulatorAngle -= MathUtils.PI * rotationTime() * dt;
        }

        while(accumulatorAngle < 0)
        {
            accumulatorAngle += MathUtils.PI2;
        }
        while(accumulatorAngle > MathUtils.PI2)
        {
            accumulatorAngle -= MathUtils.PI2;
        }

        Vector2 otherCircle = currentCircle == circlePos0 ? circlePos1 : circlePos0;
        otherCircle.x = currentCircle.x + MathUtils.cos(accumulatorAngle) * radius * 2;
        otherCircle.y = currentCircle.y + MathUtils.sin(accumulatorAngle) * radius * 2;
    }

    void setRotateDir(boolean positive)
    {
        rotatePositive = positive;
    }

    void reset()
    {
        circlePos0 = new Vector2(32, -32);
        circlePos1 = new Vector2(32, -96);
        accumulatorAngle = -MathUtils.HALF_PI;
        rotatePositive = false;
        currentCircle = circlePos0;
    }


    float rotationTime()
    {
        //120 bpm
        return 2F;
    }


    void setNextCircle()
    {
        Vector2 dir = getStillCirclePos().cpy();
        dir.sub(getRotatingCirclePos());

        accumulatorAngle = dir.angleRad();

        currentCircle = currentCircle == circlePos0 ? circlePos1 : circlePos0;
    }

    Vector2 getStillCirclePos()
    {
        return currentCircle;
    }

    Vector2 getRotatingCirclePos()
    {
        return currentCircle == circlePos0 ? circlePos1 : circlePos0;
    }

    float getRadius()
    {
        return radius;
    }

    void render(ShapeRenderer sh)
    {

        sh.setColor(Color.WHITE);
        sh.circle(circlePos0.x, circlePos0.y, radius - 1);
        sh.circle(circlePos1.x, circlePos1.y, radius - 1);

        sh.setColor(Color.GOLDENROD);
        sh.circle(circlePos0.x, circlePos0.y, radius - 2.5f);

        sh.setColor(Color.ROYAL);
        sh.circle(circlePos1.x, circlePos1.y, radius - 2.5f);

    }
}
