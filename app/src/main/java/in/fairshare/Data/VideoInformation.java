package in.fairshare.Data;

public class VideoInformation {

    public String videoTitle;
    public String videoDescp;
    public String videoUrl;

    public VideoInformation() {
    }

    public VideoInformation(String videoTitle, String videoDescp, String videoUrl) {
        this.videoTitle = videoTitle;
        this.videoDescp = videoDescp;
        this.videoUrl = videoUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescp() {
        return videoDescp;
    }

    public void setVideoDescp(String videoDescp) {
        this.videoDescp = videoDescp;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
