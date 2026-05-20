package com.shatteredpixel.shatteredpixeldungeon.items.sorting;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public class SortByType implements ItemSortStrategy {
    @Override
    public int compare(Item item1, Item item2) {
        return item1.getClass().getSimpleName().compareTo(item2.getClass().getSimpleName());
    }

    @Override
    public String getStrategyName() {
        return "Type";
    }
}
