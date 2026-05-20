package com.shatteredpixel.shatteredpixeldungeon.items.sorting;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemSortStrategyTest {

    // ── SortByName ────────────────────────────────────────────────────────────

    @Test
    void sortByName_ordersItemsCaseInsensitively() {
        NamedItem apple  = new NamedItem("apple");
        NamedItem banana = new NamedItem("Banana");
        NamedItem charm  = new NamedItem("charm");
        List<Item> items = new ArrayList<>(Arrays.asList(charm, apple, banana));

        items.sort(new SortByName());

        assertSame(apple,  items.get(0));
        assertSame(banana, items.get(1));
        assertSame(charm,  items.get(2));
    }

    @Test
    void sortByName_singleItem_unchanged() {
        NamedItem only = new NamedItem("sword");
        List<Item> items = new ArrayList<>(Collections.singletonList(only));

        items.sort(new SortByName());

        assertSame(only, items.get(0));
    }

    @Test
    void sortByName_emptyList_noException() {
        List<Item> items = new ArrayList<>();
        assertDoesNotThrow(() -> items.sort(new SortByName()));
        assertTrue(items.isEmpty());
    }

    @Test
    void sortByName_identicalNames_preservesAllElements() {
        NamedItem a = new NamedItem("sword");
        NamedItem b = new NamedItem("sword");
        List<Item> items = new ArrayList<>(Arrays.asList(a, b));

        items.sort(new SortByName());

        assertEquals(2, items.size());
    }

    // ── SortByType ────────────────────────────────────────────────────────────

    @Test
    void sortByType_ordersItemsByClassName() {
        ZetaItem  zeta  = new ZetaItem();
        AlphaItem alpha = new AlphaItem();
        List<Item> items = new ArrayList<>(Arrays.asList(zeta, alpha));

        items.sort(new SortByType());

        assertSame(alpha, items.get(0));
        assertSame(zeta,  items.get(1));
    }

    @Test
    void sortByType_sameType_preservesAllElements() {
        AlphaItem a = new AlphaItem();
        AlphaItem b = new AlphaItem();
        List<Item> items = new ArrayList<>(Arrays.asList(a, b));

        items.sort(new SortByType());

        assertEquals(2, items.size());
    }

    @Test
    void sortByType_emptyList_noException() {
        List<Item> items = new ArrayList<>();
        assertDoesNotThrow(() -> items.sort(new SortByType()));
    }

    // ── Strategy metadata ─────────────────────────────────────────────────────

    @Test
    void sortByName_strategyName_isName() {
        assertEquals("Name", new SortByName().getStrategyName());
    }

    @Test
    void sortByType_strategyName_isType() {
        assertEquals("Type", new SortByType().getStrategyName());
    }

    // ── Comparator contract (reflexivity) ─────────────────────────────────────

    @Test
    void sortByName_compareWithSelf_returnsZero() {
        NamedItem item = new NamedItem("axe");
        assertEquals(0, new SortByName().compare(item, item));
    }

    @Test
    void sortByType_compareWithSelf_returnsZero() {
        AlphaItem item = new AlphaItem();
        assertEquals(0, new SortByType().compare(item, item));
    }

    // ── Stub items ────────────────────────────────────────────────────────────

    private static class NamedItem extends Item {
        private final String name;
        NamedItem(String name) { this.name = name; }
        @Override public String name() { return name; }
    }

    private static class AlphaItem extends Item {}
    private static class ZetaItem  extends Item {}
}
