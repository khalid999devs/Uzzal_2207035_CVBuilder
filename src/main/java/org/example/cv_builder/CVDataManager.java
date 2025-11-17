package org.example.cv_builder;

public class CVDataManager {
    private static CVDataManager instance;
    private CVData currentCVData;

    private CVDataManager() {}


    public static CVDataManager getInstance() {
        if (instance == null) {
            instance = new CVDataManager();
        }
        return instance;
    }

    public CVData getCurrentCVData() {
        return currentCVData;
    }

    public void setCurrentCVData(CVData data) {
        this.currentCVData = data;
    }

    public void clearCurrentCVData() {
        this.currentCVData = null;
    }

    public boolean hasCurrentCVData() {
        return currentCVData != null;
    }
}
