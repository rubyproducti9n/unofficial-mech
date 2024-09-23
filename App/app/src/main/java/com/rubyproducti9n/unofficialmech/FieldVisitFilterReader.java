package com.rubyproducti9n.unofficialmech;

public class FieldVisitFilterReader implements FilterReader {

    @Override
    public boolean shouldInclude(Item item) {
        return "field visit".equals(item.getType());
    }
}
