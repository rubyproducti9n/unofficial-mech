package com.rubyproducti9n.unofficialmech;

public class TripFilterReader implements FilterReader {

    @Override
    public boolean shouldInclude(Item item) {
        return "trip".equals(item.getType());
    }
}
