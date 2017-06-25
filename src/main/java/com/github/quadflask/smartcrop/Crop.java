package com.github.quadflask.smartcrop;

import lombok.ToString;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@ToString
public class Crop {
    public int x, y, width, height;

    public Crop(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Crop))
            return false;

        if (obj == this)
            return true;

        Crop rhs = (Crop) obj;
        return new EqualsBuilder()
            .append(x, rhs.x)
            .append(y, rhs.y)
            .append(width, rhs.width)
            .append(height, rhs.height)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
            .append(x)
            .append(y)
            .append(width)
            .append(height)
            .toHashCode();
    }
}
