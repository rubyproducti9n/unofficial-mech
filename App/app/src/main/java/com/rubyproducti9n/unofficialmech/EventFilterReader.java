package com.rubyproducti9n.unofficialmech;

public class EventFilterReader implements FilterReader {

    @Override
    public boolean shouldInclude(Item item) {
        return "event".equals(item.getType());
    }
}
