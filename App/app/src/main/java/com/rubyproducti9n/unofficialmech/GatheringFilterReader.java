package com.rubyproducti9n.unofficialmech;

public class GatheringFilterReader implements FilterReader {

    @Override
    public boolean shouldInclude(Item item) {
        return "gathering".equals(item.getType());
    }
}
