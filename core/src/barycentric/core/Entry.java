package barycentric.core;

import bloom.Bloom;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class Entry extends ApplicationAdapter
{
	float fadeOutTimer = 0.f;


	FitViewport viewport;
	OrthographicCamera cam;

	ShapeRenderer sh;
	SpriteBatch s;

	Texture img;

	Stage stage;
	PlayerPair playerPair;

	ParticleSystem ps;

	Bloom bloom;

	float startTimer = 3;
	Texture threeTex;
	Texture twoTex;
	Texture oneTex;
	Texture goTex;

	MainMenu menu;

	@Override
	public void create ()
	{
		viewport = new FitViewport(Constants.WIDTH, Constants.HEIGHT);
		cam = (OrthographicCamera) viewport.getCamera();
		cam.update();

		sh = new ShapeRenderer();
		s = new SpriteBatch();
		playerPair = new PlayerPair();

		ps = new ParticleSystem();

		stage = new Stage(ps);

		threeTex = new Texture("3.png");
		twoTex = new Texture("2.png");
		oneTex = new Texture("1.png");
		goTex = new Texture("go.png");

		bloom = new Bloom(viewport, 1f);

		menu = new MainMenu();
	}


	@Override
	public void render ()
	{

		float dt = Gdx.graphics.getDeltaTime();

		Gdx.gl.glClearColor(0, 0, 0, 0.1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(!menu.gameStarted())
		{
			Gdx.input.setInputProcessor(null);
			menu.update(dt);
			menu.render(sh, s);

			return;
		}
		else
		{
			Gdx.input.setInputProcessor(stage);
		}


		if(startTimer < -1.f && !stage.isStageComplete())
		{
			playerPair.update(dt);

			stage.update(dt, playerPair);

			Vector2 nextPos = new Vector2();
			Vector2 target = playerPair.getStillCirclePos().cpy();
			nextPos.x = MathUtils.lerp(cam.position.x, target.x, dt);
			nextPos.y = MathUtils.lerp(cam.position.y, target.y, dt);
			cam.position.set(nextPos,0);
			cam.update();

			ps.update(dt);
		}
		else
		{
			cam.zoom = startTimer;
			if(cam.zoom < 1) cam.zoom = 1;
			cam.update();
		}






		bloom.capture();
		sh.begin(ShapeRenderer.ShapeType.Filled);
		sh.setProjectionMatrix(cam.combined);

		stage.render(sh);
		playerPair.render(sh);
		sh.end();
		bloom.render();

		ps.render();


		if(stage.isStageComplete())
		{
			fadeOutTimer += dt;
			if(fadeOutTimer > 1)
			{
				fadeOutTimer = 1;
				stage.reset();
				startTimer = 3.f;
				fadeOutTimer = 0.f;
				menu.reset();
				playerPair.reset();
				cam.position.set(playerPair.getStillCirclePos(), 0);
				ps.reset();
			}

			sh.begin(ShapeRenderer.ShapeType.Filled);
			sh.setProjectionMatrix(new Matrix4().idt());
			sh.setColor(0,0,0,fadeOutTimer);
			sh.rect(-1.5f, -1.5f, 3, 3);
			sh.end();
		}



		s.begin();
		s.setProjectionMatrix(new Matrix4().idt());
		stage.render(s);
		if(startTimer > -1.f)
		{
			if (startTimer > 2)
			{
				float ratio = (float)threeTex.getWidth() / (float)threeTex.getHeight();
				s.draw(threeTex, -0.5f * ratio / 2, -ratio / 2, 0.5f * ratio, ratio);
			}
			else if (startTimer > 1)
			{
				float ratio = (float)twoTex.getWidth() / (float)twoTex.getHeight();
				s.draw(twoTex, -0.5f * ratio / 2, -ratio / 2, 0.5f * ratio, ratio);
			}
			else if (startTimer > 0)
			{
				float ratio = (float)oneTex.getWidth() / (float)oneTex.getHeight();
				s.draw(oneTex, -0.5f * ratio / 2, -ratio / 2, 0.5f * ratio, ratio);
			}
			else
			{
				float ratio = (float)goTex.getWidth() / (float)goTex.getHeight();
				s.draw(goTex, -0.5f * ratio / 2, -ratio / 2, 0.5f * ratio, ratio);
			}
			startTimer -= dt;
		}
		s.end();
	}

	@Override
	public void dispose ()
	{
		sh.dispose();
		s.dispose();
		ps.dispose();
	}

	@Override
	public void resize(int w, int h)
	{
		viewport.update(w, h);
		viewport.apply();


		menu.resize(w, h);
		ps.resize(w, h);
	}
}
