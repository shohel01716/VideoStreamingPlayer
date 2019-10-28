package net.my.videostreamingplayer.Utils;

public interface StaticValue {
    String CODE_403 = "403";
    String CODE_404 = "404";
    String UNABLE_CONNECT = "Unable to connect";
    String INTERNET_UNABLE = "Internet might not be available!";
    String INTERNET_SETTING = "Go To Settings";
    String TEST_VIDEO_TITLE = "This is a test video";
    int MIN_BUFFER_MS = 360000;
    int MAX_BUTTER_MS = 600000;
    int BUFFER_PLAYBACK_MS = 2500;
    int REBUFFER_PLAYBACK_MS = 5000;
    int TERGET_BUFFER_BYTES = -1;
    String TEST_MPD_DASH_URL = "https://d1nxk4x0uff6m5.cloudfront.net/9c87f88d-ed53-4548-86bb-78fab96993da/dash/1min.mpd";
    String TEST_HLS_URL = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8";
    String TEST_MP4_URL = "http://techslides.com/demos/sample-videos/small.mp4";
}
