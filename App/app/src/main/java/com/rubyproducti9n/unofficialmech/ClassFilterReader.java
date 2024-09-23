package com.rubyproducti9n.unofficialmech;

public class ClassFilterReader implements FilterReader {

    @Override
    public boolean shouldInclude(Item item) {
        return "class".equals(item.getType());
    }
}
