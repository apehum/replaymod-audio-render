package com.apehum.replayaudio;

public enum AudioCodec {
    AAC("aac", "aac", "AAC"),
    MP3("libmp3lame", "mp3", "MP3"),
    OPUS("libopus", "opus", "Opus");

    public final String ffmpegCodec;
    public final String extension;
    private final String displayName;

    AudioCodec(String ffmpegCodec, String extension, String displayName) {
        this.ffmpegCodec = ffmpegCodec;
        this.extension = extension;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
