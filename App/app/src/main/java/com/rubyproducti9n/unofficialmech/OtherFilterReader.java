package com.rubyproducti9n.unofficialmech;

public class OtherFilterReader implements FilterReader {

    @Override
    public boolean shouldInclude(Item item) {
        return "other".equals(item.getType());
    }
}
