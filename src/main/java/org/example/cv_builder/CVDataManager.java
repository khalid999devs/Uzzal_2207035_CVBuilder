package org.example.cv_builder;

import org.example.cv_builder.database.CVRepository;
import org.example.cv_builder.observer.CVObserver;

import java.util.ArrayList;
import java.util.List;

public class CVDataManager {
    private static CVDataManager instance;
    private CVData currentCVData;
    private List<CVData> savedCVs;
    private List<CVObserver> observers;
    private CVRepository repository;

    private CVDataManager() {
        this.savedCVs = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.repository = CVRepository.getInstance();
    }

    public static CVDataManager getInstance() {
        if (instance == null) {
            instance = new CVDataManager();
        }
        return instance;
    }

    public void addObserver(CVObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(CVObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (CVObserver observer : observers) {
            observer.onCVListChanged();
        }
    }

    public void refreshCVList() {
        savedCVs = repository.getAllCVs();
        notifyObservers();
    }

    public List<CVData> getSavedCVs() {
        return new ArrayList<>(savedCVs);
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
