package com.shatteredpixel.shatteredpixeldungeon.items.sorting;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ItemSortStrategyTest {

    @Test
    public void sortByNameOrdersItemsCaseInsensitively() {
        NamedItem apple = new NamedItem("apple");
        NamedItem banana = new NamedItem("Banana");
        NamedItem charm = new NamedItem("charm");
        ArrayList<Item> items = new ArrayList<>(Arrays.asList(charm, apple, banana));

        items.sort(new SortByName());

        assertSame(apple, items.get(0));
        assertSame(banana, items.get(1));
        assertSame(charm, items.get(2));
    }

    @Test
    public void sortByTypeOrdersItemsByClassName() {
        ZetaItem zeta = new ZetaItem();
        AlphaItem alpha = new AlphaItem();
        ArrayList<Item> items = new ArrayList<>(Arrays.asList(zeta, alpha));

        items.sort(new SortByType());

        assertSame(alpha, items.get(0));
        assertSame(zeta, items.get(1));
    }

    @Test
    public void strategiesExposeButtonLabels() {
        assertEquals("Name", new SortByName().getStrategyName());
        assertEquals("Type", new SortByType().getStrategyName());
    }

    private static class NamedItem extends Item {
        private final String name;

        private NamedItem(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }
    }

    private static class AlphaItem extends Item {
    }

    private static class ZetaItem extends Item {
    }
}
