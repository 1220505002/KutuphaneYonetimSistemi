package com.yourname.library.pattern.observer;

import java.util.ArrayList;
import java.util.List;

public class InventorySubject {
    private List<IInventoryObserver> observers = new ArrayList<>();

    public void registerObserver(IInventoryObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IInventoryObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for(IInventoryObserver obs : observers) {
            obs.update();
        }
    }

    // Kitap eklendiğinde, silindiğinde, ödünç alındığında bu metodu çağıracaksınız.
}
