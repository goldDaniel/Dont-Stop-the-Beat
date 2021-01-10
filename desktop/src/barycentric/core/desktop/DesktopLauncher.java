package barycentric.core.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import barycentric.core.Entry;

public class DesktopLauncher
{
	public static void main (String[] arg)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 1280;
		config.height = 720;
		config.resizable = false;
		config.backgroundFPS = 60;
		config.foregroundFPS = 0;
		config.vSyncEnabled = true;

		new LwjglApplication(new Entry(), config);
	}
}
