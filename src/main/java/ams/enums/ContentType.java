package ams.enums;

import com.google.gson.annotations.SerializedName;

public enum ContentType {
    @SerializedName("text/html")
    TEXT_HTML,
    @SerializedName("text/plain")
    TEXT_PLAIN,
    @SerializedName("text/csv")
    TEXT_CSV,
    @SerializedName("video/mp4")
    VIDEO_MP4,
    @SerializedName("image/gif")
    IMAGE_GIF,
    @SerializedName("image/jpeg")
    IMAGE_JPEG,
    @SerializedName("image/png")
    IMAGE_PNG;

}
