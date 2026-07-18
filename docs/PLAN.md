# Implementation Plan: Backport FramedCompactDrawers 1.12.2 → 1.7.10

## Context / Key Findings

- **StorageDrawers is already a dependency.** [dependencies.gradle](../dependencies.gradle) declares
  `api("com.github.GTNewHorizons:StorageDrawers:2.2.26-GTNH:dev")`. We do **not** port StorageDrawers itself.
  `migrate/storagedrawers/**` contains the *decompiled classes* of that dependency jar, provided purely as an API
  reference (method/field names extracted from bytecode — verify exact signatures with your IDE's decompiler when
  unsure).
- **Only FramedCompactDrawers needs porting.** Its 1.12.2 source lives in
  `migrate/framedcompactdrawers/eutros/framedcompactdrawers/**`. The 1.7.10 target project boilerplate is at
  `src/main/java/com/mrfuzzihead/framedcompactdrawers/` (package `com.mrfuzzihead.framedcompactdrawers`, modid
  `framedcompactdrawers`).
- **API differences (1.12.2 → 1.7.10) that drive the port:**
  - No `IBlockState`/`BlockPos`. Blocks use `metadata` (int) + `world.getBlockMetadata(x,y,z)`. TileEntity lookups
    take `(IBlockAccess/World, int x, int y, int z)`.
  - No JSON block models / `IBakedModel`. Rendering is done via `IIcon` textures + a per-block
    `cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler` registered with a render ID, using Tessellator/
    `RenderBlocks`, mirroring vanilla `DrawersRenderer`/`DrawersCustomRenderer`/`ControllerRenderer` in
    `migrate/storagedrawers/client/renderer/`.
  - **No Chameleon library needed.** 1.7.10 StorageDrawers ships its own procedural box-rendering helpers:
    `com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer` / `PanelBoxRenderer` and
    `com.jaquadro.minecraft.storagedrawers.util.RenderHelper` / `RenderHelperState`. These are the direct
    replacements for Chameleon's `ChamRender`/`PanelBoxRenderer` used in the 1.12 renderer classes
    (`ControllerRenderer.java`, `ForkedDrawerRenderer.java`, `SlaveRenderer.java`).
  - **`TileEntityDrawers` (1.7.10) already has built-in material storage**: `getMaterialSide()/getMaterialFront()/
    getMaterialTrim()` + setters, backed by NBT tags `MatS`/`MatF`/`MatT`. There is **no separate `MaterialData`
    class** like in 1.12 — this eliminates the need for `MaterialModelCarrier`/`IMaterialDataCarrier` entirely for
    the drawer block. `TileEntityController` and `TileEntitySlave`, however, do **not** have material fields, so
    custom subclasses must add them (mirroring what 1.12 did for all three, but only needed for two now).
  - `ItemCustomDrawers` (1.7.10) already exists with `makeItemStack(Block, count, matSide, matTrim, matFront)` and
    a `placeBlockAt` that auto-applies `MatS`/`MatF`/`MatT` tags onto any `TileEntityDrawers` at the placed
    position — reusable as-is for the drawer item, but must be reimplemented manually (like 1.12 did) for the
    controller/slave items since their tiles aren't `TileEntityDrawers`.
  - Recipes are Java-registered (`GameRegistry.addShapedRecipe` + `OreDictionary`), not JSON.
  - Registration uses `GameRegistry.registerBlock(block, ItemClass.class, name)` and
    `GameRegistry.registerTileEntity(TileClass.class, "name")`, not `@ObjectHolder`/`IForgeRegistry`.

## Companion Document

See [TASKS.md](TASKS.md) for the granular, checkbox-level implementation checklist derived from this plan. Work
through it top-to-bottom; each item is sized for a single small-model/Cline turn.

## Phases

### Phase 1 — Scaffolding & Registration
Create `FCDCreativeTab`, `registry/ModBlocks.java`, `registry/ModItems.java`. Register blocks/items/tile entities
using 1.7.10 `GameRegistry` APIs. Wire calls into `CommonProxy.preInit()`/`init()`.

### Phase 2 — Framed Compact Drawer (block + tile + item)
New `block/BlockFramedCompactDrawer` merging `BlockDrawersCustom`'s custom-material overlay/icon logic with
`BlockCompDrawers`'s 3-slot `getDrawerSlot`/status-icon logic (no existing 1.7.10 class combines both). Backing
tile is a thin `block/tile/TileFramedCompactDrawer extends TileEntityDrawersComp` (own class purely so
`GameRegistry.registerTileEntity` has a unique mapping; no extra fields needed — materials are inherited). Item
`item/ItemFramedCompactDrawer extends ItemCustomDrawers` can reuse inherited `placeBlockAt`.

### Phase 3 — Framed Drawer Controller (block + tile + item)
New `block/BlockFramedController extends BlockController`-equivalent pattern (button-toggle key items,
`toggleShroud`/`toggleLock`/`toggleQuantify`/`toggleProtection` delegation) plus custom-material rendering/drop
logic ported from 1.12 `BlockControllerCustom`. Tile `block/tile/TileFramedController extends TileEntityController`
adds its own `matSide/matFront/matTrim` `ItemStack` fields + NBT read/write (tags `MatS`/`MatF`/`MatT`) since the
base tile lacks them. Item `item/ItemFramedController extends ItemCustomDrawers` manually applies those NBT tags
on placement (ported from 1.12 `ItemControllerCustom`).

### Phase 4 — Framed Slave (block + tile + item)
Same pattern as Phase 3: `block/BlockFramedSlave`, `block/tile/TileFramedSlave extends TileEntitySlave` (+ own
material fields), `item/ItemFramedSlave extends ItemCustomDrawers`. Slave block proxies key-button toggles to
whichever controller (vanilla or framed) it's bound to, ported from 1.12 `BlockSlaveCustom`.

### Phase 5 — Rendering
One `ISimpleBlockRenderingHandler` per block under `client/render/`, built on StorageDrawers'
`ModularBoxRenderer`/`PanelBoxRenderer`/`RenderHelper`, porting the panel-face geometry from
`ForkedDrawerRenderer.java` (compact drawer — has the 6-panel split-front layout), `ControllerRenderer.java`, and
`SlaveRenderer.java`. Register render IDs via `RenderingRegistry.registerBlockHandler` and register all block
icons via `IIconRegister` in `ClientProxy`.

### Phase 6 — Assets & Recipes
Port `lang` files, block textures (side/front/trim/overlay icons) from
`migrate/framedcompactdrawers/assets/framedcompactdrawers/**` into `src/main/resources/assets/framedcompactdrawers/
textures/blocks/`. Replace the 3 JSON recipes with a `registry/ModRecipes.java` using
`GameRegistry.addShapedRecipe` + `OreDictionary.getOres("stickWood")`, conditioned on StorageDrawers items
existing. Update `mcmod.info`.

### Phase 7 — Lifecycle Wiring & Cleanup
Call all registration/recipe/render setup from the correct `FMLPreInitializationEvent`/`FMLInitializationEvent`
phases in `FramedCompactDrawers.java` / `CommonProxy.java` / `ClientProxy.java`. Remove the placeholder `Config`
"greeting" stub (or repurpose it for real config options later). Run `get_errors` and iterate until clean.

## Further Considerations (deferred / optional)
1. Waila/Hwyla integration (`IntegrationRegistry`/`IIntegrationPlugin`/`WailaPlugin` in 1.12) — defer past MVP.
2. NEI hiding of intermediate "raw" item variants, if any exist — check after core port compiles/runs.
3. Confirm whether `TileEntity.addMapping`/`GameRegistry.registerTileEntity` name collisions matter when subclassing
   `TileEntityDrawersComp`, `TileEntityController`, `TileEntitySlave` — use unique registration names
   (`"framedcompactdrawers.framed_compact_drawer"`, etc.) to be safe.

