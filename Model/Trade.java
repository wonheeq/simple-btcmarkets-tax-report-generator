package Model;

public class Trade {
    private String date;
    private String type;
    private String asset;
    private double originalVolume;
    private double volume;
    private double price;
    private double fee;
    private double netValue;
    private double realValue;
    private boolean disabled;

    public Trade(String date, String type, String asset, double volume, double price, double fee, double value) {
        this.date = date;
        this.type = type;
        this.asset = asset;
        this.volume = Math.abs(volume);
        this.price = Math.abs(price);
        this.fee = Math.abs(fee);
        this.netValue = Math.abs(value);

        this.originalVolume = this.volume;
        this.realValue = this.netValue - this.fee;
        this.disabled = false;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getAsset() {
        return asset;
    }

    public double getVolume() {
        return volume;
    }

    public double getOriginalVolume() {
        return originalVolume;
    }

    public double getPrice() {
        return price;
    }

    public double getFee() {
        return fee;
    }
    
    public double getRealValue() {
        return realValue;
    }

    public double getNetValue() {
        return netValue;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}