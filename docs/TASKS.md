# TASKS: FramedCompactDrawers 1.7.10 Backport Checklist

Granular, atomic checklist. Do items in order within each phase; phases are ordered by dependency. Each `[ ]` item
should be small enough for a single implementation turn. File paths are relative to the repo root unless noted.

Reference source (1.12.2, port FROM): `migrate/framedcompactdrawers/eutros/framedcompactdrawers/`
Reference API (1.7.10 dependency, decompiled, port bytecode names may be approximate — verify with IDE decompiler):
`migrate/storagedrawers/`
Target project (port TO): `src/main/java/com/mrfuzzihead/framedcompactdrawers/`

---

## Phase 0 — Sanity checks before writing code

- [ ] 0.1 Open `migrate/storagedrawers/block/BlockDrawersCustom.class`,
  `migrate/storagedrawers/block/BlockCompDrawers.class`, `migrate/storagedrawers/block/tile/TileEntityDrawers.class`
  in an IDE with a Java decompiler (or use `javap -p -c`) to confirm exact method signatures before implementing
  Phase 2. Bytecode-string extraction used to write this checklist can miss generics/exceptions.
- [ ] 0.2 Same for `migrate/storagedrawers/block/tile/TileEntityController.class`,
  `migrate/storagedrawers/block/tile/TileEntitySlave.class`,
  `migrate/storagedrawers/item/ItemCustomDrawers.class` before Phase 3/4.
- [ ] 0.3 Same for `migrate/storagedrawers/client/renderer/ModularBoxRenderer.class`,
  `migrate/storagedrawers/client/renderer/PanelBoxRenderer.class`,
  `migrate/storagedrawers/util/RenderHelper.class`, `RenderHelperState.class` before Phase 5.
- [ ] 0.4 Confirm the actual Maven artifact `StorageDrawers-2.2.26-GTNH-dev.jar` is resolvable/available as a
  Gradle dependency source so your IDE can attach sources/decompiled classes (run a Gradle sync).

## Phase 1 — Scaffolding & Registration

- [ ] 1.1 Create `src/main/java/com/mrfuzzihead/framedcompactdrawers/FCDCreativeTab.java`: a `CreativeTabs` subclass
  named e.g. `"framed_compacting_drawers"`, ported from
  `migrate/framedcompactdrawers/eutros/framedcompactdrawers/FCDCreativeTab.java`. In 1.7.10 the icon-item method is
  `getTabIconItem()` returning an `Item` (not `ItemStack`) — check `CreativeTabs` base class in your MCP mappings
  and adapt.
- [ ] 1.2 Create package folder `src/main/java/com/mrfuzzihead/framedcompactdrawers/block/`.
- [ ] 1.3 Create package folder `src/main/java/com/mrfuzzihead/framedcompactdrawers/block/tile/`.
- [ ] 1.4 Create package folder `src/main/java/com/mrfuzzihead/framedcompactdrawers/item/`.
- [ ] 1.5 Create package folder `src/main/java/com/mrfuzzihead/framedcompactdrawers/registry/`.
- [ ] 1.6 Create package folder `src/main/java/com/mrfuzzihead/framedcompactdrawers/client/render/`.
- [ ] 1.7 Create `registry/ModBlocks.java` with `public static Block framedCompactDrawer;`,
  `public static Block framedDrawerController;`, `public static Block framedSlave;` and a
  `public static void register()` method (called from `CommonProxy.preInit`) — leave bodies empty/instantiate
  `null` for now, fill in after Phase 2-4 create the block classes.
- [ ] 1.8 Create `registry/ModItems.java` with `public static Item framedCompactDrawerItem;`,
  `public static Item framedDrawerControllerItem;`, `public static Item framedSlaveItem;` and a
  `public static void register()` method — fill in after Phase 2-4.
- [ ] 1.9 In `CommonProxy.preInit(FMLPreInitializationEvent event)`, add a call to `ModBlocks.register()` then
  `ModItems.register()` (after the existing `Config.synchronizeConfiguration` call, or replace it — see Phase 7).

## Phase 2 — Framed Compact Drawer (block + tile + item)

Port from: `migrate/framedcompactdrawers/eutros/framedcompactdrawers/block/BlockDrawersCustomComp.java`,
`AbstractBlockDrawersCustom.java`, `AbstractBlockCustomNonDrawer.java`.
1.7.10 API to combine: `migrate/storagedrawers/block/BlockDrawersCustom.class` (custom material overlay icons +
`getMainDrop`) and `migrate/storagedrawers/block/BlockCompDrawers.class` (3-slot `getDrawerSlot`, per-slot front
icon arrays `iconFrontInd`, disabled-slot overlay logic).

- [ ] 2.1 Create `block/tile/TileFramedCompactDrawer.java` extending
  `com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp`. No extra fields needed (material
  storage is inherited). Add only if compile requires a distinct type for tile-entity registration.
- [ ] 2.2 Create `block/BlockFramedCompactDrawer.java` extending
  `com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom`. Constructor should mirror
  `BlockDrawersCustom`'s ctor (material, "framed_compact_drawer" block name, drawerCount=3, halfDepth=false) —
  check the exact constructor signature from `BlockDrawersCustom.class` (Phase 0.1) since 1.12's ctor took
  `(String registryName, String blockName)` but 1.7.10 takes different params (block name string + count + bool
  based on decompiled evidence).
- [ ] 2.3 Override `getDrawerSlot(int side, float hitX, float hitY, float hitZ)` in `BlockFramedCompactDrawer` to
  replicate `BlockCompDrawers.getDrawerSlot` (top hit → slot 0, else left/right via `hitLeft`/`hitTop` helpers
  inherited from `BlockDrawers`).
- [ ] 2.4 Override `getDrawerCount()`/equivalent to return `3` if not already fixed by the constructor.
  Ported concept from 1.12 `getDrawerCount(state) = 3`.
- [ ] 2.5 Override `createNewTileEntity(World world, int meta)` to `return new TileFramedCompactDrawer();`.
- [ ] 2.6 Override `getSubBlocks(Item, CreativeTabs, List)` to add a single plain `ItemStack(this)` (ported from
  1.12 `func_149666_a`), matching `BlockDrawersCustom.getSubBlocks` behavior (only add when
  `config.cache.addonShowVanilla` — copy that guard if present).
  Keep `getMainDrop` overridden only if the base `BlockDrawersCustom`/`BlockCompDrawers` version doesn't already
  serialize material + sealed tile NBT correctly (compare against 1.12
  `BlockDrawersCustomComp.getMainDrop`) — port the "keep contents on break" NBT logic if missing.
- [ ] 2.7 Register icons: override
  `registerBlockIcons(IIconRegister register)` (`@SideOnly(Side.CLIENT)`), calling `super.registerBlockIcons` for
  base icons + registering the 3 per-slot front textures
  (`framedcompactdrawers:blocks/drawers_comp_raw_open_{1,2,3}`) and disabled-slot overlays
  (`framedcompactdrawers:blocks/overlay/open_{1,2,3}`) — texture keys ported from
  `CustomDrawersCompModel.Register` in
  `migrate/framedcompactdrawers/eutros/framedcompactdrawers/model/CustomDrawersCompModel.java`.
- [ ] 2.8 Create `item/ItemFramedCompactDrawer.java` extending
  `com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers`. Constructor takes the block; call
  `super(block)`. Do NOT override `placeBlockAt` — the inherited `ItemCustomDrawers.placeBlockAt` already applies
  `MatS`/`MatF`/`MatT` NBT tags onto any `TileEntityDrawers`, which `TileFramedCompactDrawer` is.
- [ ] 2.9 Fill in `ModBlocks.framedCompactDrawer = new BlockFramedCompactDrawer(); GameRegistry.registerBlock(
  ModBlocks.framedCompactDrawer, ItemFramedCompactDrawer.class, "framed_compact_drawer");` in
  `registry/ModBlocks.java`.
- [ ] 2.10 In `registry/ModItems.java`, set `ModItems.framedCompactDrawerItem = Item.getItemFromBlock(
  ModBlocks.framedCompactDrawer);` (GameRegistry.registerBlock already creates+registers the item — no separate
  `registerItem` call needed for block items in 1.7.10).
- [ ] 2.11 Call `GameRegistry.registerTileEntity(TileFramedCompactDrawer.class,
  "framedcompactdrawers.framed_compact_drawer");` in `ModBlocks.register()`.

## Phase 3 — Framed Drawer Controller (block + tile + item)

Port from: `migrate/framedcompactdrawers/eutros/framedcompactdrawers/block/BlockControllerCustom.java`,
`AbstractKeyButtonToggle.java`, `block/tile/TileControllerCustom.java`, `item/ItemControllerCustom.java`.
1.7.10 API: `migrate/storagedrawers/block/BlockController.class`,
`migrate/storagedrawers/block/tile/TileEntityController.class`,
`migrate/storagedrawers/block/BlockKeyButton.class` (verify class exists; if not present under
`migrate/storagedrawers/block/`, search for the key-button block used for shroud/lock/quantify/personal keys).

- [ ] 3.1 Create `block/tile/TileFramedController.java` extending
  `com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController`. Add fields:
  `private ItemStack matSide = null; private ItemStack matFront = null; private ItemStack matTrim = null;`
  (or whatever the 1.7.10 `ItemStack` empty-sentinel convention is — 1.7.10 uses `null` for empty, NOT
  `ItemStack.EMPTY`/`field_190927_a` like 1.12).
- [ ] 3.2 Add getters/setters on `TileFramedController`: `getMaterialSide()/setMaterialSide(ItemStack)`, same for
  Front/Trim — naming mirrors `TileEntityDrawers` for consistency with the renderer code written in Phase 5.
- [ ] 3.3 Override `readFromNBT(NBTTagCompound tag)` on `TileFramedController`: call `super.readFromNBT(tag)`,
  then read `MatS`/`MatF`/`MatT` compound tags via `ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"))`
  etc., guarded by `tag.hasKey("MatS")` (ported from 1.12 `TileControllerCustom.func_145839_a` /
  `MaterialData.readFromNBT`).
- [ ] 3.4 Override `writeToNBT(NBTTagCompound tag)` on `TileFramedController`: call `super.writeToNBT(tag)`, then
  write each non-null material `ItemStack` as a compound tag under `MatS`/`MatF`/`MatT` via
  `matSide.writeToNBT(new NBTTagCompound())`.
- [ ] 3.5 Create `block/BlockFramedController.java` extending
  `com.jaquadro.minecraft.storagedrawers.block.BlockController` (verify this is a valid extension point — if
  `BlockController` is not designed for subclassing, check for a `BlockKeyButtonToggle`/similar intermediate class
  in `migrate/storagedrawers/block/` first).
- [ ] 3.6 Override `createTileEntity`/`createNewTileEntity` equivalent to return `new TileFramedController();`.
- [ ] 3.7 Port the key-toggle dispatch logic from 1.12 `BlockControllerCustom.toggle(World, BlockPos, EntityPlayer,
  Item)` and `toggle(..., EnumKeyType)` into `BlockFramedController`, using 1.7.10
  `com.jaquadro.minecraft.storagedrawers.core.ModItems` (`drawerKey`, `shroudKey`, `quantifyKey`, `personalKey`)
  and `com.jaquadro.minecraft.storagedrawers.block.EnumKeyType` (or whatever the 1.7.10 equivalent enum is named —
  check `migrate/storagedrawers/block/` for an `EnumKeyType` class; if absent, check nested types on
  `BlockController`/`BlockKeyButton`).
- [ ] 3.8 Port `onBlockActivated`/`func_180639_a` equivalent from 1.12 `BlockControllerCustom` — handle key-item
  toggle first, then facing check, then `interactPutItemsIntoInventory(player)` on the tile.
- [ ] 3.9 Port the facing-on-place / neighbor-facing-correction logic from 1.12
  `AbstractBlockCustomNonDrawer`/`BlockControllerCustom.func_176213_c` (`onNeighborBlockChange`/
  `onBlockAdded` in 1.7.10 naming) if `BlockController` doesn't already handle it — check
  `BlockController.class` decompile first, since it may already auto-orient via `getTileEntitySafe` +
  `setDirection`.
- [ ] 3.10 Override `registerBlockIcons`/equivalent to register the overlay textures ported from
  `CustomControllerModel.Register` in
  `migrate/framedcompactdrawers/eutros/framedcompactdrawers/model/CustomControllerModel.java`:
  `blocks/raw_side`, `blocks/drawer_controller_raw_front`, `blocks/overlay/shading_controller_{trim,bold_trim,
  face}`, `blocks/overlay/handle`.
- [ ] 3.11 Override the drop logic (`getMainDrop`/`removedByPlayer` equivalent) so breaking the block produces an
  `ItemStack` carrying `MatS`/`MatF`/`MatT` tags read off `TileFramedController`, using
  `ItemCustomDrawers.makeItemStack(this, 1, matSide, matTrim, matFront)` (reuse the 1.7.10 static helper directly).
- [ ] 3.12 Create `item/ItemFramedController.java` extending `ItemCustomDrawers`. Override `placeBlockAt(...)`
  (manual re-implementation, NOT calling `super.placeBlockAt`, since `TileFramedController` is not a
  `TileEntityDrawers`): place the block via `world.setBlock(x, y, z, block, metadata, 3)`, call
  `block.onBlockPlacedBy(...)` if needed, then fetch `(TileFramedController) world.getTileEntity(x, y, z)` and set
  `MatS`/`MatF`/`MatT` from the stack's NBT tag if present — ported from 1.12 `ItemControllerCustom.placeBlockAt`
  + `defaultPlaceBlocKAt`.
- [ ] 3.13 Fill in `ModBlocks.framedDrawerController = new BlockFramedController(); GameRegistry.registerBlock(...,
  ItemFramedController.class, "framed_drawer_controller");` and `GameRegistry.registerTileEntity(
  TileFramedController.class, "framedcompactdrawers.framed_drawer_controller");`.

## Phase 4 — Framed Slave (block + tile + item)

Port from: `migrate/framedcompactdrawers/eutros/framedcompactdrawers/block/BlockSlaveCustom.java`,
`block/tile/TileSlaveCustom.java`, `item/ItemSlaveCustom.java`.
1.7.10 API: `migrate/storagedrawers/block/BlockSlave.class`,
`migrate/storagedrawers/block/tile/TileEntitySlave.class`.

- [ ] 4.1 Create `block/tile/TileFramedSlave.java` extending
  `com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave`. Add the same `matSide/matFront/matTrim`
  fields + NBT read/write as `TileFramedController` (Phase 3.1-3.4), duplicated (no shared base class required,
  but consider extracting a tiny shared helper/interface if convenient — optional, not blocking).
- [ ] 4.2 Create `block/BlockFramedSlave.java` extending `com.jaquadro.minecraft.storagedrawers.block.BlockSlave`.
- [ ] 4.3 Override `createTileEntity`/`createNewTileEntity` to return `new TileFramedSlave();`.
- [ ] 4.4 Port the key-toggle proxy logic from 1.12 `BlockSlaveCustom.toggle(...)`: look up the slave tile's bound
  controller position (`TileEntitySlave.getControllerPos()` or equivalent 1.7.10 accessor — verify name in Phase
  0.2), get the `Block` at that position, and if it's `BlockController` or `BlockFramedController`, delegate the
  toggle call to it.
- [ ] 4.5 Register the watched-blocks set (vanilla `controllerslave`/slave block target) if the key-button
  right-click detection in 1.7.10 StorageDrawers works differently than 1.12's `AbstractKeyButtonToggle` event-bus
  approach — check whether `BlockKeyButton`/`TileEntityKeyButton` in 1.7.10 already dispatches to
  `IExtendedBlockClickHandler.onBlockClicked` (confirmed present at
  `migrate/storagedrawers/block/IExtendedBlockClickHandler.class`) instead of needing a custom
  `PlayerInteractEvent` listener — if so, implement `IExtendedBlockClickHandler` on `BlockFramedSlave` and
  `BlockFramedController` instead of porting `AbstractKeyButtonToggle`'s event subscriber.
- [ ] 4.6 Override `registerBlockIcons`/equivalent using texture keys ported from `CustomSlaveModel.Register`:
  `blocks/raw_side`, `blocks/slave_raw_top_bottom`, `blocks/overlay/shading_side`.
- [ ] 4.7 Override drop logic to build the dropped `ItemStack` via `ItemCustomDrawers.makeItemStack(this, 1,
  matSide, matTrim, matFront)` from `TileFramedSlave`'s fields.
- [ ] 4.8 Create `item/ItemFramedSlave.java` extending `ItemCustomDrawers`, with a manually-implemented
  `placeBlockAt` identical in structure to `ItemFramedController` (Phase 3.12) but casting to
  `TileFramedSlave` — ported from 1.12 `ItemSlaveCustom`.
- [ ] 4.9 Fill in `ModBlocks.framedSlave = new BlockFramedSlave(); GameRegistry.registerBlock(...,
  ItemFramedSlave.class, "framed_slave");` and `GameRegistry.registerTileEntity(TileFramedSlave.class,
  "framedcompactdrawers.framed_slave");`.

## Phase 5 — Rendering

Reference StorageDrawers rendering classes: `migrate/storagedrawers/client/renderer/ControllerRenderer.class`
(pattern for `ISimpleBlockRenderingHandler` using `ModularBoxRenderer`), `DrawersCustomRenderer.class`,
`DrawersRenderer.class`, `PanelBoxRenderer.class`, and 1.12 geometry source
`migrate/framedcompactdrawers/eutros/framedcompactdrawers/model/{ForkedDrawerRenderer,ControllerRenderer,
SlaveRenderer}.java` (panel-face coordinates/order to replicate, translated from `ChamRender`/`PanelBoxRenderer`
calls to 1.7.10 `RenderHelper`/`PanelBoxRenderer` calls).

- [ ] 5.1 Add three new render-ID `int` fields to `ClientProxy.java` (or a small
  `client/render/FCDRenderIds.java` holder): `framedCompactDrawerRenderId`, `framedControllerRenderId`,
  `framedSlaveRenderId`, each assigned via
  `RenderingRegistry.getNextAvailableRenderId()` during `preInit`.
- [ ] 5.2 Create `client/render/RenderFramedCompactDrawer.java` implementing
  `cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler`. Constructor takes nothing; instantiate a
  `PanelBoxRenderer` (or use `RenderHelper.instances.get()` pattern seen in
  `migrate/storagedrawers/client/renderer/DrawersRenderer.class`) as a field.
- [ ] 5.3 Implement `renderInventoryBlock(Block, int metadata, int modelId, RenderBlocks renderer)` on
  `RenderFramedCompactDrawer` — simple cube render with default icons (ported concept from
  `ControllerRenderer.class`'s `renderInventoryBlock`).
- [ ] 5.4 Implement `renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
  RenderBlocks renderer)` on `RenderFramedCompactDrawer`: fetch `TileFramedCompactDrawer`, resolve icons the same
  way `DrawersCustomRenderer.class` does (material stack → `Block.getBlockFromItem(stack.getItem())` →
  `block.getIcon(int side, int meta)`, falling back to default icons if material stack is null), then call a new
  `renderBasePass(...)` helper ported from 1.12 `ForkedDrawerRenderer.renderBasePass` — translate each
  `renderHelper.setRenderBounds(...); renderHelper.renderFace(...)` pair to the 1.7.10
  `PanelBoxRenderer`/`RenderHelper` equivalents (same six sub-face split-front geometry: 4 quadrant front faces +
  cross-trim faces).
- [ ] 5.5 Port the overlay/translucent pass (handle icon, per-slot disabled overlay, trim shadow) from 1.12
  `ForkedDrawerRenderer.renderOverlayPass` into `RenderFramedCompactDrawer` — call it as a second render pass in
  `renderWorldBlock` (1.7.10 has no separate `canRenderInPass`/multi-pass unless the block declares
  `isOpaqueCube()==false`/uses `renderWorldBlock` return value; check whether `BlockDrawersCustom`'s
  `canRenderInPass`/`getRenderBlockPass` needs overriding to get a second pass — mirror
  `BlockDrawersCustom.class`'s `getRenderBlockPass`/`canRenderInPass` methods seen in the decompile).
- [ ] 5.6 Implement `shouldRender3DInInventory(int modelId)` returning `true` and `getRenderId()` returning
  `ClientProxy.framedCompactDrawerRenderId` on `RenderFramedCompactDrawer`.
- [ ] 5.7 Repeat 5.2-5.6 for `client/render/RenderFramedController.java`, porting geometry from 1.12
  `ControllerRenderer.java` (single split-front panel, not 4-way split) and icon resolution from
  `TileFramedController`'s material fields.
- [ ] 5.8 Repeat 5.2-5.6 for `client/render/RenderFramedSlave.java`, porting geometry from 1.12
  `SlaveRenderer.java` (plain 6-face box with side/top-bottom icon split + side-shadow overlay).
- [ ] 5.9 In `ClientProxy.preInit()` (or a dedicated method called from it), call
  `RenderingRegistry.registerBlockHandler(new RenderFramedCompactDrawer());` and same for Controller/Slave.
- [ ] 5.10 In `ClientProxy` (or directly in each Block's `@SideOnly(Side.CLIENT) registerBlockIcons`), verify all
  icons referenced in Phase 2.7 / 3.10 / 4.6 are actually registered — cross-check the full icon list against
  Phase 6's texture files so nothing 404s at runtime.

## Phase 6 — Assets & Recipes

- [ ] 6.1 Copy all textures from
  `migrate/framedcompactdrawers/assets/framedcompactdrawers/textures/blocks/` into
  `src/main/resources/assets/framedcompactdrawers/textures/blocks/` (flat copy, 1.7.10 doesn't need
  `models/`/`blockstates/` folders).
  - [ ] 6.1a
    List the source textures directory first (`migrate/framedcompactdrawers/assets/framedcompactdrawers/textures/
    blocks/`) to get the exact file list before copying, since it wasn't fully enumerated during planning.
- [ ] 6.2 Copy `migrate/framedcompactdrawers/assets/framedcompactdrawers/lang/*.lang` into
  `src/main/resources/assets/framedcompactdrawers/lang/` unchanged (1.7.10 lang format is identical `.lang` k=v
  syntax).
- [ ] 6.3 Verify/add lang keys match the actual registered block/item unlocalized names chosen in Phases 2-4
  (1.7.10 tile lang keys look like `tile.framed_compact_drawer.name=...` — no modid prefix segment the same way
  1.12 does; double check against how `GameRegistry.registerBlock`'s third arg composes the unlocalized name in
  this MCP mapping/Forge version).
- [ ] 6.4 Copy `migrate/framedcompactdrawers/logo.png` into `src/main/resources/` (or wherever `mcmod.info`'s
  `logoFile` should point) and update `src/main/resources/mcmod.info` `logoFile` field.
- [ ] 6.5 Update `src/main/resources/mcmod.info`: set `description`, `authorList`, `url`, `credits` fields
  appropriately (currently placeholder ExampleMod text — see [mcmod.info](../src/main/resources/mcmod.info)),
  and add `"requiredMods": ["storagedrawers"]` / `dependencies` entries so Forge enforces the StorageDrawers
  dependency at load time.
- [ ] 6.6 Create `registry/ModRecipes.java` with a `public static void register()` method.
- [ ] 6.7 Port the framed-compact-drawer recipe from
  `migrate/framedcompactdrawers/assets/framedcompactdrawers/recipes/framed_compacting_drawer.json`: 3x3 shaped,
  border = `OreDictionary` `"stickWood"`, center = `storagedrawers:compdrawers` (or whatever the actual 1.7.10 SD
  compdrawers block/item registry name is — check `migrate/storagedrawers/core/ModBlocks.class` for the exact
  field/registered name), output = `ModBlocks.framedCompactDrawer`. Use
  `GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.framedCompactDrawer), "///", "/X/", "///",
  '/', "stickWood", 'X', new ItemStack(<compdrawers block>)));` guarded by a null/`Item.getItemFromBlock(...) !=
  null` check so a missing dependency doesn't crash recipe registration.
- [ ] 6.8 Port the framed-drawer-controller recipe from
  `migrate/framedcompactdrawers/assets/framedcompactdrawers/recipes/framed_drawer_controller.json` the same way,
  substituting the vanilla `storagedrawers` controller block/item as the center ingredient.
- [ ] 6.9 Port the framed-slave recipe from
  `migrate/framedcompactdrawers/assets/framedcompactdrawers/recipes/framed_slave.json` the same way, substituting
  the vanilla `controllerslave` block/item as the center ingredient (name confirmed via
  `BlockSlaveCustom.watchedBlocks.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("storagedrawers",
  "controllerslave")))` in the 1.12 source).
- [ ] 6.10 Call `ModRecipes.register()` from `CommonProxy.init(FMLInitializationEvent event)`.

## Phase 7 — Lifecycle Wiring & Cleanup

- [ ] 7.1 In `CommonProxy.preInit`, confirm final call order: `Config.synchronizeConfiguration(...)` (or its
  replacement, see 7.2) → `ModBlocks.register()` → `ModItems.register()`.
- [ ] 7.2 Decide whether to keep `Config.java`'s placeholder `greeting` string option. If not needed, remove the
  `greeting`-related code from `Config.java` and its log line in `CommonProxy.preInit` (currently logs
  `Config.greeting`), or repurpose `Config` for a real future option (e.g. a "show in creative tab" toggle) —
  don't leave dead placeholder code behind.
- [ ] 7.3 In `ClientProxy.preInit` (override, calling `super.preInit()`), add render-ID assignment (Phase 5.1) and
  render handler registration (Phase 5.9). Confirm `ClientProxy` actually overrides `preInit`/`init` — currently
  it has no overrides at all (see [ClientProxy.java](../src/main/java/com/mrfuzzihead/framedcompactdrawers/
  ClientProxy.java)).
- [ ] 7.4 In `CommonProxy.init`, add `ModRecipes.register()` call (Phase 6.10).
- [ ] 7.5 Double check `FramedCompactDrawers.java`'s `@Mod` annotation `dependencies` string includes
  `"required-after:storagedrawers;"` so Forge enforces load order (currently missing — compare to 1.12's
  `dependencies = "required-after:storagedrawers;required-after:chameleon;"`, dropping the chameleon requirement
  since it's unused in 1.7.10).

## Phase 8 — Validation

- [ ] 8.1 Run `get_errors` (or `./gradlew compileJava`) on every new/edited file under
  `src/main/java/com/mrfuzzihead/framedcompactdrawers/` and fix compile errors, iterating phase by phase rather
  than all at once.
- [ ] 8.2 Run `./gradlew build` (or the project's equivalent task) once compilation is clean.
- [ ] 8.3 Launch `runClient` and manually verify: the 3 items appear in the creative tab, each block places with
  correct default facing, right-clicking with a StorageDrawers custom-material item variant reskins the block,
  breaking drops the reskinned item with contents preserved, and key items (shroud/lock/quantify/personal) work
  on `BlockFramedController` the same as vanilla `BlockController`.
- [ ] 8.4 Verify the 3 recipes appear in-game (e.g. via NEI, already a runtime dependency per
  [dependencies.gradle](../dependencies.gradle)).

## Deferred / Optional (do not block MVP)

- [ ] D.1 Port Waila/Hwyla integration (`IntegrationRegistry`, `IIntegrationPlugin`, `WailaPlugin` in
  `migrate/framedcompactdrawers/eutros/framedcompactdrawers/integration/`) once a Waila-compatible mod for 1.7.10
  is confirmed available/desired.
- [ ] D.2 Investigate NEI hiding of any intermediate "raw" texture-only item variants, if the port ends up
  creating any (unlikely given the icon-based approach, but check after Phase 5).

