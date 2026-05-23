# SQA Report — Shattered Pixel Dungeon v3.0.2
**Role:** SQA & Testing Lead  
**Author:** Robin Kim  
**Scope:** All three quality improvements (Inventory Sorting, Combat Statistics, Mob Logging)

---

## 1. Tools & Infrastructure

Three SQA tools were configured across the `core` and `SPD-classes` modules.  
All tools run together with one command:

```bash
./gradlew sqa
```

| Tool | Purpose | Version |
|------|---------|---------|
| **JaCoCo** | Code coverage measurement | 0.8.11 |
| **PMD** | Cyclomatic complexity & code style | 6.55.0 |
| **SpotBugs** | Bug pattern detection | 4.8.6 |
| **JUnit 5** | Unit testing framework | 5.10.0 |
| **Mockito** | Mocking for unit tests | 5.5.0 |

### Individual commands

```bash
./gradlew :core:test                # Run tests + JaCoCo coverage report
./gradlew :core:pmdMain             # PMD complexity analysis
./gradlew :core:spotbugsMain        # SpotBugs bug detection
./gradlew jacocoAggregateReport     # Combined coverage across all modules
```

### Report locations

| Report | Path |
|--------|------|
| Test results | `core/build/reports/tests/test/index.html` |
| JaCoCo coverage | `build/reports/jacoco/aggregate/html/index.html` |
| PMD | `core/build/reports/pmd/main.html` |
| SpotBugs | `core/build/reports/spotbugs/main/spotbugs.html` |

---

## 2. Test Results

**31 tests — all passing, 0 failures.**

| Test Class | Tests | Covers |
|-----------|-------|--------|
| `SmokeTest` | 1 | JUnit 5 + JaCoCo pipeline verification |
| `ItemSortStrategyTest` | 11 | Inventory Sorting (Strategy Pattern) |
| `MobLoggerTest` | 7 | Mob Logging system |
| `CombatStatsTest` | 12 | Combat Statistics calculations |
| **Total** | **31** | |

### Key test cases

**ItemSortStrategyTest** — verifies correctness and edge cases of sorting strategies:
- Case-insensitive alphabetical ordering (`SortByName`)
- Class-name ordering (`SortByType`)
- Empty list, single item, identical names (edge cases)
- Comparator reflexivity contract (`compare(x, x) == 0`)

**CombatStatsTest** — verifies accuracy of combat metric calculations:
- Division-by-zero guard in `getAccuracyRate()` (no attacks attempted → returns 0, not NaN)
- Full-hit, half-hit, zero-hit accuracy scenarios
- `reset()` clears all counters correctly
- Counter accumulation over multiple events

**MobLoggerTest** — verifies logging behaviour and file I/O:
- Log count starts at 0, increments on each call
- Log file is created and messages are appended correctly
- No exception thrown under normal usage

---

## 3. PMD — Cyclomatic Complexity Before / After

PMD was run on the codebase before and after the Inventory Sorting improvement to verify that the Strategy Pattern measurably reduces complexity.

### Before (original codebase)

| Class | Total CC | Highest method CC |
|-------|----------|-------------------|
| `items/Item.java` | 127 | 19 (`collect(Bag)`) |
| `items/Heap.java` | 107 | 16 |
| `items/armor/Armor.java` | 203 | 28 |
| `items/weapon/Weapon.java` | 136 | 36 |

Notable violation on `Item.java`:
```
Item.java:203  CyclomaticComplexity  collect(Bag)  CC = 19
Item.java:203  NPathComplexity       collect(Bag)  NPath = 2400  (threshold: 200)
```

**Project-wide: 1,660 PMD violations** before any improvements.

### After (post Inventory Sorting implementation)

The three new sorting classes introduced by the Strategy Pattern:

| Class | CC |
|-------|----|
| `ItemSortStrategy.java` (interface) | 1 |
| `SortByName.java` | 1 |
| `SortByType.java` | 1 |

**PMD violations on new sorting classes: 0**

Each strategy is a single focused method — complexity stays at 1 regardless of how many strategies are added. This is the key maintainability benefit of the Strategy Pattern: adding a new sort order requires a new class with CC = 1, not modifying an existing high-complexity method.

---

## 4. SpotBugs — Bug Detection

SpotBugs was run across `core` and `SPD-classes` after each improvement was merged.

### Finding: Resource leak in `MobLogger.java`

| Field | Detail |
|-------|--------|
| **Class** | `com.shatteredpixel.shatteredpixeldungeon.logging.MobLogger` |
| **Bug type** | `OBL_UNSATISFIED_OBLIGATION` (resource leak) |
| **Severity** | Priority 1 (High) |
| **Location** | `MobLogger.log()` |

**Root cause:** `FileWriter` and `PrintWriter` were opened inside a regular `try` block without a `finally` clause. If an exception occurred mid-write, the streams would never be closed, causing a file handle leak.

**Original code:**
```java
try {
    FileWriter fw = new FileWriter(LOG_FILE, true);
    PrintWriter pw = new PrintWriter(fw);
    pw.println(message);
    pw.close();     // never reached if exception occurs
} catch (IOException e) { ... }
```

**Fix applied (try-with-resources):**
```java
try (FileWriter fw = new FileWriter(LOG_FILE, true);
     PrintWriter pw = new PrintWriter(fw)) {
    pw.println(message);
} catch (IOException e) { ... }
```

**Verification:** SpotBugs re-run after fix — `MobLogger` no longer appears in the report. All 7 `MobLoggerTest` tests continue to pass.
