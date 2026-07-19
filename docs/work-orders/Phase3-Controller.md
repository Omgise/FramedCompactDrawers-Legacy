# Work Order: Phase 3 — Framed Controller

**Subsystems:** block, tile, item, cross-mod API (SecurityProvider)
**Clusters:** Controller (Tightly-coupled: BlockFramedController ↔ TileFramedController ↔ ItemFramedController)
**Status:** ✅ Verified (compile) · ⏳ Dev client & behavior pending

## Source Files (1.12.2 reference)

- `migrate/framedcompactdrawers/src/main/java/com/mrfuzzihead/framedcompactdrawers/block/BlockControllerCustom.java`
- `migrate/framedcompactdrawers/src/main/java/com/mrfuzzihead/framedcompactdrawers/block/tile/TileControllerCustom.java`
- `migrate/framedcompactdrawers/src/main/java/com/mrfuzzihead/framedcompactdrawers/item/ItemControllerCustom.java`

## Target Files (1.7.10 implementation)

- `block/BlockFramedController.java`
- `block/tile/TileFramedController.java`
- `item/ItemFramedController.java`

## Dependent Classes (what breaks if this changes)

- `TileFramedSlave` — references `TileFramedController.toggle()` API (Phase 4)
- `registry/ModBlocks` — instantiates `BlockFramedController`
- `registry/ModItems` — instantiates `ItemFramedController`

## API Delta Rulebook Rows Applied

| System | 1.12.2 | 1.7.10 |
|--------|--------|--------|
| Coordinates | `BlockPos` | `int x, int y, int z` |
| Block state | `IBlockState` | `int metadata` |
| Entity/TE data | `NBTTagCompound` (same) | `NBTTagCompound` — same signature |
| Loot/drops | `getMainDrop` | `ItemStack.makeItemStack` with material NBT |
| Cross-mod API | `Chameleon` (out of scope) | N/A |

## Key 1.7.10 Decisions

- **`writeToNBT`**: returns `void` (1.7.10 `TileEntity.writeToNBT` signature), NOT `NBTTagCompound`
- **`createNewTileEntity`**: covariant return type `TileFramedController` (not `TileEntity`)
- **`ModItems.drawerKey`**: renamed to `ModItems.upgradeLock` in 1.7.10
- **`player.getHeldItem()`**: not available; use `player.inventory.getCurrentItem()`
- **`Item.getStack(int)`**: not an override — removed from `ItemFramedController`
- **`ISecurityProvider`**: import required from `com.jaquadro.minecraft.storagedrawers.api.security`
- **`LockAttribute.LOCK_POPULATED`**: used as the lock attribute for `toggleLock()`

## Acceptance Criteria

- [x] All three classes compile cleanly
- [ ] Dev client loads without crash
- [ ] Block places and interacts with key items (upgradeLock, shroudKey, quantifyKey, personalKey)
- [ ] Material data persists via `MatS`/`MatT`/`MatF` NBT tags
- [ ] `ItemFramedController.makeDropStack()` builds correct drops with material data

## Notes

- `TileEntityController` base has NO material fields (unlike `TileEntityDrawers`) — hence `matSide/matTrim/matFront` custom fields are needed
- `BlockFramedController.getRenderBlockPass()` returns 1 (overlay pass) — Phase 5 renderer will render base pass via parent `BlockController`
- `BlockFramedController.canRenderInPass()` returns true for all passes — allows rendering in both passes
