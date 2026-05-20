package com.shatteredpixel.shatteredpixeldungeon.items.sorting;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import java.util.Comparator;

public interface ItemSortStrategy extends Comparator<Item> {
    String getStrategyName();
}
