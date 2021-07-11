package link.joy.cctv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pedro.rtmp.utils.ConnectCheckerRtmp;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RtmpCamera1 camera = new RtmpCamera1(getApplicationContext(), new ConnectCheckerRtmp() {
            @Override
            public void onConnectionStartedRtmp(String s) {

            }

            @Override
            public void onConnectionSuccessRtmp() {

            }

            @Override
            public void onConnectionFailedRtmp(String s) {

            }

            @Override
            public void onNewBitrateRtmp(long l) {

            }

            @Override
            public void onDisconnectRtmp() {

            }

            @Override
            public void onAuthErrorRtmp() {

            }

            @Override
            public void onAuthSuccessRtmp() {

            }
        });
        if (camera.prepareAudio() && camera.prepareVideo())
            camera.startStream("rtmp://a.rtmp.youtube.com/live2/xxx");
    }
}