package net.my.videostreamingplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;

import net.my.videostreamingplayer.App.MyApp;
import net.my.videostreamingplayer.Utils.ToastUtil;

import static net.my.videostreamingplayer.Utils.StaticValue.BUFFER_PLAYBACK_MS;
import static net.my.videostreamingplayer.Utils.StaticValue.CODE_403;
import static net.my.videostreamingplayer.Utils.StaticValue.CODE_404;
import static net.my.videostreamingplayer.Utils.StaticValue.INTERNET_SETTING;
import static net.my.videostreamingplayer.Utils.StaticValue.INTERNET_UNABLE;
import static net.my.videostreamingplayer.Utils.StaticValue.MAX_BUTTER_MS;
import static net.my.videostreamingplayer.Utils.StaticValue.MIN_BUFFER_MS;
import static net.my.videostreamingplayer.Utils.StaticValue.REBUFFER_PLAYBACK_MS;
import static net.my.videostreamingplayer.Utils.StaticValue.TERGET_BUFFER_BYTES;
import static net.my.videostreamingplayer.Utils.StaticValue.TEST_MPD_DASH_URL;
import static net.my.videostreamingplayer.Utils.StaticValue.TEST_VIDEO_TITLE;
import static net.my.videostreamingplayer.Utils.StaticValue.UNABLE_CONNECT;

public class PlayerActivity extends AppCompatActivity implements Player.EventListener, PlaybackPreparer {

    private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector = new DefaultTrackSelector();
    private CircularProgressView progress_live;
    private ImageButton rewind, play, forward;
    private TextView title;
    private MediaSource drmMediaSource;
    private DefaultBandwidthMeter bandwidthMeter;
    private DefaultAllocator allocator;
    private DefaultLoadControl loadControl;
    private DataSource.Factory dataSourceFactory;
    private long ms = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        progress_live = findViewById(R.id.progress_live);
        title = findViewById(R.id.title);
        title.setText(TEST_VIDEO_TITLE);

        rewind = findViewById(R.id.rewind);
        play = findViewById(R.id.play);
        forward = findViewById(R.id.forward);

        rewind.setOnClickListener(view -> setRewind());
        play.setOnClickListener(view ->setPlayPause());
        forward.setOnClickListener(view -> setForward());

        simpleExoPlayerView = findViewById(R.id.player_view);
        initPlayer(TEST_MPD_DASH_URL);
    }

    private void initPlayer(String url) {
        bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        allocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);

        loadControl = new DefaultLoadControl(allocator, MIN_BUFFER_MS, MAX_BUTTER_MS, BUFFER_PLAYBACK_MS, REBUFFER_PLAYBACK_MS, TERGET_BUFFER_BYTES, true);
        //***
        player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
        simpleExoPlayerView.setPlayer(player);

        dataSourceFactory = new DefaultDataSourceFactory(MyApp.getAppContext(),
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
        drmMediaSource = buildMediaSource(Uri.parse(url), dataSourceFactory);

        player.addListener(this);
        simpleExoPlayerView.setPlaybackPreparer(this);

        player.prepare(drmMediaSource, false, false);
    }

    private MediaSource buildMediaSource(Uri uri, DataSource.Factory dataSourceFactory) {
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        Log.e("onTracksChanged", String.valueOf(mappedTrackInfo.getRendererCount()));
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                progress_live.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_READY:
                progress_live.setVisibility(View.GONE);
                break;
            case Player.STATE_ENDED:
                play.setImageResource(R.drawable.exo_controls_play);
                break;
            default:
                break;
        }
    }

    private void setPlayPause() {
        if (player == null) {
            Log.e("player:: ", "player null");
            return;
        }

        if (player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            play.setImageResource(R.drawable.exo_controls_play);
        } else {
            player.setPlayWhenReady(true);
            play.setImageResource(R.drawable.exo_controls_pause);
        }
    }

    public void setRewind() {
        if (player == null) {
            Log.e("player:: ", "player null");
            return;
        }

        long currentDuration = player.getCurrentPosition();
        long seekPosition = currentDuration - ms;//ms

        if (seekPosition < 0) {
            player.seekTo(0);
        } else {
            player.seekTo(seekPosition);
        }
    }

    public void setForward() {
        if (player == null) {
            Log.e("player:: ", "player null");
            return;
        }

        long duration = player.getDuration();
        long currentDuration = player.getCurrentPosition();
        long seekPosition = currentDuration + ms;//ms
        if (duration > seekPosition) {
            player.seekTo(seekPosition);
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.e("ExoPlayer: Player Error", String.valueOf(error));
        if (error.getSourceException() != null) {
            if (error.getSourceException().getMessage().contains(CODE_403) || error.getSourceException().getMessage().contains(CODE_404)) {
                ToastUtil.showToastMessage(getString(R.string.not_found), true);
            } else if (error.getSourceException().getMessage().contains(UNABLE_CONNECT)) {
                Snackbar.make(title, INTERNET_UNABLE, Snackbar.LENGTH_LONG).setAction(INTERNET_SETTING, v -> startActivity(new Intent(Settings.ACTION_SETTINGS))).show();
            }
        } else {
            ToastUtil.showToastMessage(getString(R.string.playback_error), true);
        }
        player.setPlayWhenReady(false);
        play.setImageResource(R.drawable.exo_controls_play);
        progress_live.setVisibility(View.GONE);
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {
    }

    @Override
    public void preparePlayback() {
        player.prepare(drmMediaSource, false, false);
    }

    @Override
    public void onStop() {
        player.setPlayWhenReady(false);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }
}
