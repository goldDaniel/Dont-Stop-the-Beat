package barycentric.core.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import barycentric.core.Entry;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {

                GwtApplicationConfiguration config = new GwtApplicationConfiguration(true);
                config.antialiasing = true;

                return config;
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new Entry();
        }
}