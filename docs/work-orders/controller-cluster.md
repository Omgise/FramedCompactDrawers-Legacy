# Work Order: Framed Controller Cluster

## Phase 3

### Source Files (1.12.2)
- `eutros/framedcompactdrawers/block/BlockControllerCustom.java`
- `eutros/framedcompactdrawers/block/AbstractKeyButtonToggle.java` (1.12.2 only - not needed in 1.7.10, toggles handled inline)
- `eutros/framedcompactdrawers/block/tile/TileControllerCustom.java`
- `eutros/framedcompactdrawers/item/ItemControllerCustom.java`

### Target Files (1.7.10)
- `block/BlockFramedController.java`
- `block/tile/TileFramedController.java`
- `item/ItemFramedController.java`

### Dependent Classes
- `BlockController` — parent block class; extends BlockContainer
- `TileEntityController` — parent tile class; provides toggleShroud/toggleQuantify/toggleLock/toggleProtection methods
- `ItemCustomDrawers` — parent item class; provides makeItemStack helper, base placeBlockAt
- `ModItems` (SD) — drawerKey, shroudKey, quantifyKey, personalKey references
- `StorageDrawers.securityRegistry` — for personal key security provider
- `StorageDrawers.proxy.controllerRenderID` — render type

### API Delta Rows Applied
- Coordinates (`World, int x, int y, int z`)
- Block state (raw int metadata for direction)
- Loot/drops (`getMainDrop` with NBT)
- Entity/TE data (manual NBT `MatS`/`MatT`/`MatF`)
- Event subscription (`MinecraftForge.EVENT_BUS.register` instead of `@EventBusSubscriber`)

### Key 1.7.10 Design Decisions
- **No AbstractKeyButtonToggle** — 1.7.10 handles key-button toggles directly in `onBlockActivated` via item checks (same pattern as `BlockController`)
- **TileFramedController** adds its own `matSide`/`matTrim`/`matFront` ItemStack fields because `TileEntityController` does NOT descend from `TileEntityDrawers`
- **ItemFramedController** overrides `placeBlockAt` because `TileFramedController` is NOT a `TileEntityDrawers` instance, so inherited material application won't work
- NBT tag names: `MatS`, `MatT`, `MatF` — matching TileEntityDrawers convention

### Acceptance Criteria
1. All three classes compile with zero errors
2. `gradlew build` succeeds
3. `runClient` loads without crash
4. Controller block places with direction from player yaw (via inherited `onBlockPlacedBy`)
5. Key items (shroud/quantify/personal/drawer) toggle correctly on controller
6. Drop preserves material data via `getMainDrop`

### Status
⬜ Not started