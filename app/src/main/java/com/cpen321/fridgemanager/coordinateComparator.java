package com.cpen321.fridgemanager;

import android.graphics.RectF;

import java.util.Comparator;

/**
 * Created by wyc_j on 2016-11-06.
 * This class is a custom comparator used to sort the textblock objects by their coordinate position
 */

public class CoordinateComparator implements Comparator<OcrGraphic> {

    @Override
    public int compare(OcrGraphic g1, OcrGraphic g2) {
        RectF rect1 = new RectF(g1.getTextBlock().getBoundingBox());
        RectF rect2 = new RectF(g2.getTextBlock().getBoundingBox());
        if(rect1.top > rect2.top)
            return 1;
        else return -1;
    }
}
