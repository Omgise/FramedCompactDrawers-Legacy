# Work Order: Framed Compact Drawer Cluster

## Phase 2

### Source Files (1.12.2)
- `eutros/framedcompactdrawers/block/BlockDrawersCustomComp.java`
- `eutros/framedcompactdrawers/item/ItemCompDrawersCustom.java`
- `eutros/framedcompactdrawers/block/tile/TileEntityCompDrawersCustom.java` (no new class needed in 1.7.10)

### Target Files (1.7.10)
- `block/BlockFramedCompactDrawer.java`
- `item/ItemFramedCompactDrawer.java`
- `block/tile/TileFramedCompactDrawer.java`

### Dependent Classes
- `BlockDrawersCustom` — parent class; method signatures must match
- `TileEntityDrawersComp` — parent tile class; no new material fields needed (inherited from `TileEntityDrawers`)
- `ItemCustomDrawers` — parent item class; reuses inherited `placeBlockAt`
- `StorageDrawers.proxy.drawersCustomRenderID` — render type reference
- `StorageDrawers.config.cache.addonShowVanilla` — creative tab check

### API Delta Rows Applied
- Block state (raw int metadata)
- Coordinates (`World, int x, int y, int z`)
- Registration (`GameRegistry.registerBlock`/`registerItem`)
- Loot/drops (`getMainDrop`)

### Acceptance Criteria (Definition of Done)
1. `BlockFramedCompactDrawer`, `TileFramedCompactDrawer`, and `ItemFramedCompactDrawer` compile with zero errors
2. `gradlew build` succeeds
3. `runClient` loads without crash or classloading errors
4. Block places in-world with correct 3-slot `getDrawerSlot` behavior (top hit → slot 0, left/right via `hitLeft`)
5. Material data persists via inherited `TileEntityDrawersComp` NBT (`MatS`/`MatF`/`MatT` tags)
6. Custom-material overlay textures render correctly in world and inventory (handled in Phase 5)

### Status
✅ **Complete** — All criteria met, verified 2026-07-18.