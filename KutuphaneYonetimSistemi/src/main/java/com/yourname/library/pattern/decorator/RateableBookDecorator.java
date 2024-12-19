// RateableBookDecorator.java
package com.yourname.library.pattern.decorator;

public class RateableBookDecorator extends BookDecorator {
    private double rating = 0;
    private int ratingCount = 0;

    public RateableBookDecorator(BookComponent decoratedBook) {
        super(decoratedBook);
    }

    public void addRating(double newRating) {
        ratingCount++;
        rating = (rating*(ratingCount-1) + newRating) / ratingCount;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " | Rating: " + rating + " (" + ratingCount + " votes)";
    }
}
