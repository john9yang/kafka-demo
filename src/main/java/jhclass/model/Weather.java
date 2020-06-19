package jhclass.model;

public class Weather {
    private double lng;
    private double lat;
    private double avgTmpF;
    private double avgTmpC;
    private String date;
    private String hashGeo;

    public String getHashGeo() {
        return hashGeo;
    }

    public void setHashGeo(String hashGeo) {
        this.hashGeo = hashGeo;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAvgTmpF() {
        return avgTmpF;
    }

    public void setAvgTmpF(double avgTmpF) {
        this.avgTmpF = avgTmpF;
    }

    public double getAvgTmpC() {
        return avgTmpC;
    }

    public void setAvgTmpC(double avgTmpC) {
        this.avgTmpC = avgTmpC;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
