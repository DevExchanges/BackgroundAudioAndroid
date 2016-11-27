package info.devexchanges.backgroundaudio;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;
    private int currentState;
    private MediaBrowserCompat mediaBrowserCompat;

    private MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                MediaControllerCompat mediaControllerCompat = new MediaControllerCompat(MainActivity.this, mediaBrowserCompat.getSessionToken());
                mediaControllerCompat.registerCallback(mediaControllerCompatCallback);
                setSupportMediaController(mediaControllerCompat);
                getSupportMediaController().getTransportControls().playFromMediaId(String.valueOf(R.raw.beo_dat_may_troi__anh_tho), null);

            } catch( RemoteException e ) {
                e.printStackTrace();
            }
        }
    };

    private MediaControllerCompat.Callback mediaControllerCompatCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            if( state == null ) {
                return;
            }

            switch( state.getState() ) {
                case PlaybackStateCompat.STATE_PLAYING: {
                    currentState = STATE_PLAYING;
                    break;
                }
                case PlaybackStateCompat.STATE_PAUSED: {
                    currentState = STATE_PAUSED;
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlay = (Button) findViewById(R.id.btn_play);

        mediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, BackgroundAudioService.class),
                connectionCallback, getIntent().getExtras());
        mediaBrowserCompat.connect();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( currentState == STATE_PAUSED ) {
                    getSupportMediaController().getTransportControls().play();
                    currentState = STATE_PLAYING;
                } else {
                    if( getSupportMediaController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
                        getSupportMediaController().getTransportControls().pause();
                    }

                    currentState = STATE_PAUSED;
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( getSupportMediaController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            getSupportMediaController().getTransportControls().pause();
        }

        mediaBrowserCompat.disconnect();
    }
}
