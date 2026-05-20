# SQA Baseline Report — Shattered Pixel Dungeon v3.0.2
**Captured:** 2026-05-20  
**Tools:** PMD 6.55.0 · SpotBugs 4.8.6 · JaCoCo 0.8.11  
**Scope:** `core` + `SPD-classes` modules  
**Run command:** `./gradlew sqa`

---

## 1. PMD — Cyclomatic Complexity (Before)

> Used to justify the **Inventory Sorting (Strategy Pattern)** improvement.  
> After implementation, re-run `./gradlew :core:pmdMain` and compare the
> complexity of the affected class(es) against the values below.

### Top offenders in `items/` package

| Class | Total CC | Highest single method CC |
|-------|----------|--------------------------|
| `items/Item.java` | 127 | 19 (`collect(Bag)`) |
| `items/Heap.java` | 107 | 16 |
| `items/Generator.java` | 92 | 17 (`random(Category)`) |
| `items/armor/Armor.java` | 203 | 28 |
| `items/weapon/Weapon.java` | 136 | 36 |
| `items/weapon/melee/MeleeWeapon.java` | 116 | 32 |
| `items/wands/Wand.java` | 148 | 33 |
| `items/rings/Ring.java` | 101 | 13 |

### Notable violations on `Item.java`
```
Item.java:60   CyclomaticComplexity  class total = 127  (highest 19)
Item.java:203  CyclomaticComplexity  collect(Bag)       CC = 19
Item.java:203  NPathComplexity       collect(Bag)       NPath = 2400  (threshold 200)
Item.java:203  ExcessiveMethodLength collect(Bag)       too long
```

### Project-wide PMD summary
| Metric | Value |
|--------|-------|
| Total PMD violations (`core`) | **1,660** |
| CyclomaticComplexity violations | high |
| NPathComplexity violations | high |

---

## 2. SpotBugs — Bug Patterns (Before)

> Used as a general code-quality baseline across all three improvements.  
> Re-run `./gradlew :core:spotbugsMain` after each improvement and note
> whether the bug count in the changed classes decreases.

| Module | Report rows (bugs) |
|--------|--------------------|
| `core` | ~65 |
| `SPD-classes` | run `:SPD-classes:spotbugsMain` to measure |

HTML reports:
- `core/build/reports/spotbugs/main/spotbugs.html`
- `SPD-classes/build/reports/spotbugs/main/spotbugs.html`

---

## 3. JaCoCo — Code Coverage (Before)

> Baseline is **0 %** — no unit tests existed before this assignment.  
> Coverage will increase as each improvement adds its test suite.

| Module | Line coverage (before) |
|--------|------------------------|
| `core` | 0 % |
| `SPD-classes` | 0 % |

Aggregate HTML: `build/reports/jacoco/aggregate/html/index.html`

---

## How to Compare After Each Improvement

```bash
# Run everything
./gradlew sqa

# Or run tools individually
./gradlew :core:pmdMain         # PMD  → core/build/reports/pmd/main.html
./gradlew :core:spotbugsMain    # SpotBugs → core/build/reports/spotbugs/main/spotbugs.html
./gradlew jacocoAggregateReport # Coverage → build/reports/jacoco/aggregate/html/index.html
```

### What to look for (Inventory Sorting improvement)

1. Open `core/build/reports/pmd/main.html`
2. Filter by the new sorting class (e.g. `InventorySorter.java`)
3. Its CyclomaticComplexity per method should be **≤ 5** (each strategy is one small method)
4. Compare against `Item.java:collect(Bag)` CC = 19 above — this is the "before"
