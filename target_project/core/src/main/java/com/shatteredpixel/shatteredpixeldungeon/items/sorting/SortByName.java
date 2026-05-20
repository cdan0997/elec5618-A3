package com.shatteredpixel.shatteredpixeldungeon.items.sorting;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public class SortByName implements ItemSortStrategy {
    @Override
    public int compare(Item item1, Item item2) {
        return item1.name().compareToIgnoreCase(item2.name());
    }

    @Override
    public String getStrategyName() {
        return "Name";
    }
}
