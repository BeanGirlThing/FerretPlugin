package life.ferret.ferretPlugin.FerretCoreTools;

public class statusIndicator {

    private int status = 2;
    private String featureName;
    private boolean isDependency;

    public statusIndicator(String featureName, boolean isDependency) {
        this.featureName = featureName;
        this.isDependency = isDependency;
    }

    public boolean isDependency() {
        return isDependency;
    }

    public String getFeatureName() {
        return featureName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
