package com.yourname.library.pattern.observer;

public class InventoryUIObserver implements IInventoryObserver {
    @Override
    public void update() {
        System.out.println("Envanter değişti, UI güncellenmeli.");
        // Burada gerçek uygulamada UI bileşenleri yenilenir.
    }
}
