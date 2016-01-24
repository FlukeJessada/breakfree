package catalinc.games.breakfree.components;

import catalinc.games.breakfree.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AudioPlayer implements World.Observer {
    private final Map<World.Event, Sound> soundForEvent;
    private boolean muted;

    public AudioPlayer(String configFilePath) {
        Properties props = new Properties();
        try {
            try (Reader reader = Gdx.files.internal(configFilePath).reader()) {
                props.load(reader);
            }
        } catch (IOException e) {
            throw new RuntimeException("unable to load audio player configuration: " + e.getMessage());
        }

        soundForEvent = new HashMap<>();
        for (String name : props.stringPropertyNames()) {
            String event = name.substring(name.lastIndexOf('.') + 1);
            String path = props.getProperty(name);
                soundForEvent.put(World.Event.valueOf(event),
                        Gdx.audio.newSound(Gdx.files.internal(path)));
        }
    }

    public void onNotify(World.Event event) {
        if (muted) return;

        Sound sound = soundForEvent.get(event);
        if (sound != null) {
            sound.play();
        }
    }

    public void dispose() {
        soundForEvent.values().forEach(Sound::dispose);
    }

    public void mute() {
        muted = true;
    }

    public void unmute() {
        muted = false;
    }
}