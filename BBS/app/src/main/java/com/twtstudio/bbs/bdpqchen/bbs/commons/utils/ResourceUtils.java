package com.twtstudio.bbs.bdpqchen.bbs.commons.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * Created by bdpqchen on 17-4-19.
 */

public final class ResourceUtils {

    public static int getColor(Context context, int resourceId){
        return ContextCompat.getColor(context, resourceId);
    }

}
