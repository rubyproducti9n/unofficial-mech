package com.rubyproducti9n;

import android.content.Context;
import android.widget.Toast;

public class DataRetrieval {

    public static String generateUrl(){
        return getProtocol() + getHost() + getPath();
    }

public static String getProtocol(){
        return "http://";
}

public static String getHost(){
    StringBuilder builder = new StringBuilder();

    // Define base values for each segment (replace with slightly different values if needed)
    int[] baseSegments = {190, 165, 20, 75};

    // Define offsets for each segment (adjust slightly to avoid exceeding valid IP ranges)
    int[] offsets = {2, 3, 3, 3};

    for (int i = 0; i < 4; i++) {
        // Apply segment-specific offset to base value
        int segment = baseSegments[i] + offsets[i];

        // Enforce valid IP range (optional, adjust as needed)
        segment = Math.min(segment, 255); // Clamp to maximum IP value
        segment = Math.max(segment, 0);   // Clamp to minimum IP value

        // Convert segment to String WITHOUT leading zeros (important!)
        String segmentString = String.valueOf(segment);

        builder.append(segmentString);
        builder.append(".");
    }

    // Remove the trailing dot
    builder.deleteCharAt(builder.length() - 1);

    return builder.toString();
}

public static String getPath(){
        return "/dashboard/";
}

}
