# Backport Plan: Framed Compact Drawers (1.12.2 → 1.7.10)

## Process Alignment

This plan is the mod-specific companion to the process defined in `.agents/skills/*` and `.clinerules/*`. Status
tracking and cross-doc reconciliation live in `docs/master-migration-spec.md` — treat that as the canonical index
and this file as the detailed narrative reference. Concretely:

- `docs/class-audit.md` (from `audit-classes`) already enumerates all 26 in-scope 1.12.2 classes by subsystem and
  cluster — consult it before starting a phase rather than re-deriving scope from the "Files to create" lists
  below.
- `docs/master-migration-spec.md` (from the `02-master-migration-spec` standing rule) holds Forge/MCP versions,
  the subsystem inventory, and per-phase status/DoD — update its status table as each phase passes `verify-phase`.
- Per-cluster work orders belong under `docs/work-orders/<cluster>.md` (per `03-work-orders.md`) — create one the
  first time a phase actually starts implementation, not speculatively ahead of time.
- `verify-phase` results must be recorded in `docs/verification-log.md`, not just reported inline in chat.
- The "API Delta — Key Mappings" table below is **mod-specific** and supplements — does not replace —
  `.clinerules/01-api-delta-1.12.2-1.7.10.md`, which stays mod-agnostic per `00-standing-rules.md`.
- **Caveat on the table below** (cross-checked row-by-row against `01-api-delta-1.12.2-1.7.10.md` — no
  contradictions found): most rows use well-established SRG names (e.g. `ItemStack.func_190926_b`,
  `NBTTagCompound.func_74764_b`, `World.func_180495_p`/`func_175625_s`/`func_180501_a`), but three —
  `TileEntity.func_190560_a`, `World.func_175713_t`, and `World.func_175704_b` — could not be independently
  confirmed against a real 1.12.2 MCP export (no decompiled Forge/FML source is checked into this repo to verify
  against, only the StorageDrawers dependency reference under `migrate/storagedrawers/`). Per the "never guess a
  mapped name" standing rule, treat those three specifically as **unverified leads**, not settled fact — confirm
  via decompiler/javadoc before relying on them, and expect compile-driven discovery to be the real source of
  truth per the MCP mappings row in the rulebook.
- The cross-check also found 3 rows below (`EnumFacing`→`ForgeDirection`, the `ItemStack` empty-sentinel pair,
  and `@EventBusSubscriber`/`@SubscribeEvent`→manual `EVENT_BUS.register()`) that are genuinely mod-agnostic, not
  FCD-specific — they've been **promoted into `01-api-delta-1.12.2-1.7.10.md`** as new rows so future ports can
  reuse them. They're left in the table below too for at-a-glance context; if the two ever drift, the rulebook
  wins.
- `docs/PLAN.md`/`docs/TASKS.md` describe the same 7-phase breakdown as this file from an earlier drafting pass;
  they are substantively consistent with the corrections below (both already reflect that `TileEntityDrawers` has
  native material storage while `TileEntityController`/`TileEntitySlave` do not). No technical conflict found, but
  keep edits to phase scope synced across both, and let `docs/master-migration-spec.md` arbitrate if they ever
  diverge.

## Agent Instructions

You are backporting the **Framed Compact Drawers** mod from Minecraft 1.12.2 to 1.7.10. Target package: `com.mrfuzzihead.framedcompactdrawers`. Work through the phases below **in order** — later phases depend on earlier ones. After completing each phase, run the verification steps before moving on. Do not touch the Waila integration; it is explicitly out of scope.

---

## Scope Summary

Framed Compact Drawers extends **Storage Drawers 1.7.10** by adding framed variants of three SD blocks:

| Framed Block             | Extends                                      |
|--------------------------|----------------------------------------------|
| Framed Drawer Controller | `BlockController` / `TileEntityController`   |
| Framed Compacting Drawer | `BlockCompDrawers` / `TileEntityDrawersComp` |
| Framed Slave             | `BlockSlave` / `TileEntitySlave`             |

The 1.12.2 source has ~28 Java files (excluding Waila integration). The 1.12.2 mod depends on **Chameleon** for
model rendering, which does not exist as a separate library in 1.7.10 — but 1.7.10 StorageDrawers ships its own
equivalent procedural rendering helpers (`ModularBoxRenderer`/`PanelBoxRenderer`/`RenderHelper`), so rendering
uses `ISimpleBlockRenderingHandler` + `IIcon` + those helpers rather than handwritten GL11 geometry. See Phase 5.

**StorageDrawers itself is already a dependency** (`api("com.github.GTNewHorizons:StorageDrawers:2.2.26-GTNH:dev")`
in `dependencies.gradle`) — it is not ported. `migrate/storagedrawers/**` contains decompiled reference classes
for that dependency jar only; verify exact method/field signatures with your IDE's decompiler when in doubt.

---

## Package Layout

Target package: `com.mrfuzzihead.framedcompactdrawers` (already established in boilerplate).

Sub-packages:

- `block` / `block.tile` — blocks and tile entities
- `item` — block-placement items
- `client/render` — custom renderers (`ISimpleBlockRenderingHandler` implementations)
- `registry` — dedicated registration classes (`ModBlocks`, `ModItems`, `ModRecipes`), called from the proxy
  rather than inlined into it

Class naming follows a `Framed*` convention (`BlockFramedController`, `TileFramedSlave`,
`ItemFramedCompactDrawer`, etc.) rather than the 1.12.2 source's `*Custom` suffix convention, to match the
existing 1.7.10 boilerplate.

---

## API Delta — Key Mappings

| 1.12.2 (FCD source)                                             | 1.7.10 (target)                                                    |
|-----------------------------------------------------------------|--------------------------------------------------------------------|
| `net.minecraftforge.fml.common.Mod`                             | `cpw.mods.fml.common.Mod`                                          |
| `net.minecraftforge.fml.common.SidedProxy`                      | `cpw.mods.fml.common.SidedProxy`                                   |
| `ForgeRegistries` / `Register<Block>`                           | `GameRegistry.registerBlock()` / `GameRegistry.registerItem()`     |
| `ExtendedBlockState` / `IUnlistedProperty`                      | Raw int metadata (0–15), `world.getBlockMetadata(x,y,z)`           |
| `BlockPos`                                                      | `int x, int y, int z`                                              |
| `IBlockState` / `BlockStateContainer`                           | `int metadata`                                                     |
| `TileEntity.func_190560_a` (registry)                           | `GameRegistry.registerTileEntityWithAlternatives()`                |
| `Chameleon.instance.modelRegistry` / `ChamRender` / `ChamModel` | `ISimpleBlockRenderingHandler` / custom renderer classes / `IIcon` |
| `ModelRegistryEvent`                                            | `RenderingRegistry.registerBlockHandler()` in proxy                |
| `@EventBusSubscriber` / `@SubscribeEvent`                       | Direct calls / `MinecraftForge.EVENT_BUS.register()`               |
| `ResourceLocation` for textures                                 | `String` paths for `registerBlockIcons(IIconRegister)`             |
| `EnumFacing` (1.12.2)                                           | `ForgeDirection` (1.7.10)                                          |
| `world.isRemote`                                                | `world.isRemote` (unchanged)                                       |
| `world.func_180495_p(pos)`                                      | `world.getBlock(x,y,z)`                                            |
| `world.func_175625_s(pos)`                                      | `world.getTileEntity(x,y,z)`                                       |
| `world.func_180501_a(pos, state, flag)`                         | `world.setBlock(x,y,z, block, meta, flag)`                         |
| `world.func_175713_t(pos)`                                      | `world.destroyBlock(x,y,z, dropBlock)`                             |
| `world.func_175704_b(pos, pos)`                                 | `world.playSoundAtEntity(...)` / `world.playSoundEffect(...)`      |
| `ItemStack.func_190926_b()` (isEmpty)                           | `stack == null`                                                    |
| `ItemStack.field_190927_a` (EMPTY)                              | `null`                                                             |
| `NBTTagCompound.func_74764_b(key)` (hasKey)                     | `tag.hasKey(key)`                                                  |
| `NBTTagCompound.func_74775_l(key)` (getCompoundTag)             | `tag.getCompoundTag(key)`                                          |
| `IExtendedBlockState.withProperty()`                            | NBT on tile entity for material data                               |
| `ThreadLocal<Boolean>`                                          | static `boolean` flag (simplification)                             |
| `Supplier<Boolean>` lambda                                      | `new Boolean()` or static field                                    |

---

## Phase 1 — Scaffolding & Registration

**Goal:** Compile-clean mod, dev client loads, and a registration skeleton exists for all three framed blocks/
items/tiles (bodies filled in during Phases 2–4).

**Files to create:**
- `FCDCreativeTab.java` — new creative tab `framed_compacting_drawers`, icon = framed compact drawer item
- `registry/ModBlocks.java` — holds block instances, called from `CommonProxy.preInit()`
- `registry/ModItems.java` — holds item instances, called from `CommonProxy.preInit()`

**Files to modify:**
- `CommonProxy.java` — add `preInit()` calls to instantiate/register blocks & items via `GameRegistry`; add
  `init()` calls for render registration (Phase 5)

**Verify:**
- `gradlew build` succeeds
- Dev client launches, mod appears in mod list

**API delta rows applied:** Registration (`GameRegistry.registerBlock/registerItem`), `CreativeTabs`.

**Key 1.7.10 decisions:**
- No `ExtendedBlockState` — material data stored in NBT on the tile entity where needed (see Phases 3–4);
  direction stored in metadata (0–5, `ForgeDirection` ordinals)
- No `@EventBusSubscriber` — registration happens directly in proxy `preInit()`
- No `TileEntity.func_190560_a` — use `GameRegistry.registerTileEntity(TileClass.class, "name")`, with a unique
  name per class (see Open Questions)
- Registration lives in dedicated `registry/` classes rather than inline in the proxy, to keep `CommonProxy`
  focused on lifecycle wiring

---

## Phase 2 — Framed Compact Drawer (block + tile + item)

**Goal:** The compacting drawer block registers, places, and stores per-instance materials correctly.

**Files to create:**
- `block/BlockFramedCompactDrawer.java` — merges `BlockDrawersCustom`'s custom-material overlay/icon logic with
  `BlockCompDrawers`'s 3-slot `getDrawerSlot`/status-icon logic. No existing 1.7.10 class already combines both,
  so this is a genuine merge, not a straight port.
- `block/tile/TileFramedCompactDrawer.java` — thin `extends TileEntityDrawersComp`. **No new material fields** —
  see the correction note below Phase 4 for why.
- `item/ItemFramedCompactDrawer.java` — extends `ItemCustomDrawers`. Reuses the inherited `placeBlockAt` as-is
  (no override needed).

**API delta rows applied:** Coordinates, Block state (metadata).

**Key 1.7.10 decisions:**
- `TileEntityDrawers` (the 1.7.10 base) already provides `getMaterialSide()/getMaterialFront()/getMaterialTrim()`
  + setters backed by `MatS`/`MatF`/`MatT` NBT tags — no `MaterialData` class or carrier interface needed here
- `TileFramedCompactDrawer` exists only so `GameRegistry.registerTileEntity` has a unique class mapping distinct
  from vanilla `TileEntityDrawersComp`

---

## Phase 3 — Framed Drawer Controller (block + tile + item)

> **Corrected from initial draft** — `TileEntityController` has no built-in material fields (unlike
> `TileEntityDrawers`), so this phase genuinely needs the custom NBT plumbing originally (and correctly) scoped
> for it.

**Goal:** Controller block behaves correctly (interaction, key toggling, drops) and renders with custom side/trim/
front materials.

**Files to create:**
- `block/BlockFramedController.java` — extends the `BlockController` pattern; button-toggle key items
  (`toggleShroud`/`toggleLock`/`toggleQuantify`/`toggleProtection`, delegated to the tile); `onBlockPlacedBy`
  (direction from player yaw); `onBlockActivated`; `getMainDrop` (preserve material data); `isSideSolid`. Custom-
  material rendering/drop logic ported from 1.12.2 `BlockControllerCustom`.
- `block/tile/TileFramedController.java` — extends `TileEntityController`. Adds its own `matSide`, `matTrim`,
  `matFront` `ItemStack` fields **because the base tile lacks them** (unlike `TileEntityDrawers`, which has
  material storage built in). NBT read/write under tags `MatS`/`MatF`/`MatT`, matching the convention used by
  `TileEntityDrawers` for consistency.
- `item/ItemFramedController.java` — extends `ItemCustomDrawers`. **Must manually override `placeBlockAt`** to
  apply the `MatS`/`MatF`/`MatT` NBT tags onto the placed `TileFramedController`, since the inherited
  `placeBlockAt` only auto-applies materials to tiles that are instances of `TileEntityDrawers` (which this tile
  is not). Ported from 1.12.2 `ItemControllerCustom`.
- `block/AbstractKeyButtonToggle.java` — 1.7.10: intercept `PlayerInteractEvent` via
  `MinecraftForge.EVENT_BUS.register()`, fake button press, delegate to the tile's `toggle()`.

**API delta rows applied:** Coordinates, Block state (metadata), Loot/drops, Entity/TE data (NBT serialization).

**Key 1.7.10 decisions:**
- `fakeButtonPress` uses `world.playSoundEffect` / `world.setBlock` for button activation
- `getMainDrop` builds an `ItemStack` with NBT tags containing material data (matches
  `ItemCustomDrawers.makeItemStack` pattern)
- No `IExtendedBlockState`/`getExtendedState` — material display is handled entirely by the renderer, reading
  straight off the tile's fields

---

## Phase 4 — Framed Slave (block + tile + item)

> **Corrected from initial draft** — same reasoning as Phase 3: `TileEntitySlave` has no built-in material
> storage, so it needs its own fields. The drawer block/tile (originally lumped into this phase) has been moved
> out — see Phase 2 note below.

**Goal:** Slave block proxies key-button toggles to whichever controller (vanilla or framed) it's bound to, and
renders with custom materials.

**Files to create:**
- `block/BlockFramedSlave.java` — `bindController` delegation, `onBlockActivated`, key-button toggle proxying to
  the bound controller. Ported from 1.12.2 `BlockSlaveCustom`.
- `block/tile/TileFramedSlave.java` — extends `TileEntitySlave`. Adds its own `matSide`/`matTrim`/`matFront`
  `ItemStack` fields + NBT read/write (`MatS`/`MatF`/`MatT`) — same rationale as `TileFramedController`.
- `item/ItemFramedSlave.java` — extends `ItemCustomDrawers`, manually overrides `placeBlockAt` to apply the NBT
  material tags on placement (same pattern as `ItemFramedController` — `TileFramedSlave` is not a
  `TileEntityDrawers`).

**API delta rows applied:** Coordinates, Block state (metadata), Loot/drops, Entity/TE data (NBT serialization).

**Key 1.7.10 decisions:**
- Same `AbstractKeyButtonToggle` interception pattern as the controller, but the toggle delegates outward to
  whatever controller the slave is currently bound to rather than handling it locally
- Registration name for `TileFramedSlave` must be unique (e.g. `"framedcompactdrawers.framed_slave"`) to avoid
  colliding with `TileEntitySlave`'s own registration — see Open Questions.

---

## Note on Phase 2 (Framed Compact Drawer)

> **Correction:** the compacting drawer's tile entity extends `TileEntityDrawersComp` → `TileEntityDrawers`,
> which in 1.7.10 **already has built-in material storage** (`getMaterialSide()/getMaterialFront()/
> getMaterialTrim()` + setters, backed by `MatS`/`MatF`/`MatT`). There is no `MaterialData` class or
> `MaterialModelCarrier`/`IMaterialDataCarrier` needed for this block — that indirection was 1.12.2-only and
> doesn't need porting here.
>
> Practical effect: `block/tile/TileFramedCompactDrawer.java` should stay a thin `extends TileEntityDrawersComp`
> with **no new material fields** — it exists purely so `GameRegistry.registerTileEntity` has a unique class to
> map. Likewise `item/ItemFramedCompactDrawer.java` (`extends ItemCustomDrawers`) can **reuse the inherited
> `placeBlockAt` as-is** — no override needed, since the base implementation already auto-applies `MatS`/`MatF`/
> `MatT` to any `TileEntityDrawers` at the placement site. The block itself, `block/BlockFramedCompactDrawer.java`,
> still needs custom logic, but it's a merge of two existing 1.7.10 patterns rather than a new one: it combines
> `BlockDrawersCustom`'s custom-material overlay/icon logic with `BlockCompDrawers`'s 3-slot `getDrawerSlot`/
> status-icon logic, since no existing 1.7.10 class already does both.

---

## Phase 5 — Rendering (Base + Overlay Passes)

> **Corrected from initial draft** — 1.7.10 StorageDrawers does **not** require hand-rolled GL11 panel geometry.
> It ships its own procedural box-rendering helpers that are the direct replacement for Chameleon's `ChamRender`/
> `PanelBoxRenderer`, used by the 1.12.2 renderer classes. This is a port of existing geometry logic onto those
> helpers, not a from-scratch GL11 implementation — meaningfully less new rendering code than originally scoped.

**Goal:** All three framed blocks render with side/trim/front material textures (base pass) plus shading/handle
overlays (overlay pass), instead of vanilla SD textures.

**Files to create (under `client/render/`):**
- `client/render/ControllerRenderer.java` — extends `ISimpleBlockRenderingHandler`; base pass renders material
  textures, overlay pass (render pass 1) adds trim shadow + handle + face shadow. Built on
  `com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer` / `PanelBoxRenderer` and
  `com.jaquadro.minecraft.storagedrawers.util.RenderHelper` / `RenderHelperState`, porting the geometry from
  1.12.2 `ControllerRenderer.java`.
- `client/render/DrawersCompRenderer.java` — base pass renders material textures + dynamic 3-slot front face;
  overlay pass adds trim shadow + handle + face shadow + disabled-slot indicators. Ports the 6-panel split-front
  layout from 1.12.2 `ForkedDrawerRenderer.java`, built on the same `ModularBoxRenderer`/`PanelBoxRenderer` helpers.
- `client/render/SlaveRenderer.java` — base pass renders material textures; overlay pass adds side shadow on all
  faces. Ports 1.12.2 `SlaveRenderer.java` onto the same helpers.

**Files to modify:**
- `CommonProxy.java` / `ClientProxy.java` — register renderers via `RenderingRegistry.registerBlockHandler()`;
  register all block icons via `IIconRegister` in `ClientProxy`

**API delta rows applied:** Rendering/models (`IIcon` + `ISimpleBlockRenderingHandler`), Rendering
(`ChamRender`/Chameleon → `ModularBoxRenderer`/`PanelBoxRenderer`/`RenderHelper`).

**Key 1.7.10 decisions:**
- `ModularBoxRenderer`/`PanelBoxRenderer`/`RenderHelper` replace Chameleon's `ChamRender`/`PanelBoxRenderer` —
  **no** direct GL11 panel-geometry code needs to be written from scratch; the helpers already exist in the SD
  1.7.10 dependency jar (reference their decompiled signatures under `migrate/storagedrawers/client/renderer/`)
- Each renderer implements `ISimpleBlockRenderingHandler` with `renderInventoryBlock`, `renderWorldBlock`,
  `shouldUseRenderPass`, `useRenderPass`, `shouldRender3D`
- `IIcon` arrays for side/trim/front/overlay textures, loaded via `registerBlockIcons(IIconRegister)` on each
  block
- `shouldUseRenderPass(renderPass)` returns `true` for pass 1 (overlay); `useRenderPass(renderPass)` sets
  `GL11.glEnable(GL11.GL_ALPHA_TEST)` for that pass

---

## Phase 6 — Recipes & Assets

**Goal:** Blocks are craftable and fully textured/localized, matching 1.12.2 FCD's recipes and art assets.

**Files to create:**
- `registry/ModRecipes.java` — replaces the 3 JSON shaped recipes from 1.12.2 with
  `GameRegistry.addShapedRecipe(...)` calls, using `OreDictionary.getOres("stickWood")` (or the appropriate
  dictionary entries) in place of JSON ingredient tags. Recipes should be conditioned on the relevant
  StorageDrawers items existing at registration time.

**Assets to port** (source: `migrate/framedcompactdrawers/assets/framedcompactdrawers/**` from the 1.12.2 tree
→ target: `src/main/resources/assets/framedcompactdrawers/`, following 1.7.10 SD's flat texture layout rather
than 1.12.2's JSON-model-driven layout):
- Block textures (side/front/trim/overlay icons) → `textures/blocks/`
- Item textures (if any placement-item icons exist separately from block icons) → `textures/items/`
- `lang/en_US.lang` (1.7.10 `.lang` format, not 1.12.2 `.json` lang files) — port block/item names
- Update `mcmod.info` with final mod metadata

**Files to modify:**
- `CommonProxy.java` — add `ModRecipes.init()` (or equivalent) call during `FMLInitializationEvent`

**Key 1.7.10 decisions:**
- No JSON blockstates/models to port — icon registration in Phase 5 already covers texture wiring; this phase is
  purely about getting the raw texture files, lang strings, and recipes into the 1.7.10 asset layout
- Recipes are Java code, not data files — 1.12.2's `assets/framedcompactdrawers/recipes/*.json` (or
  `data/.../recipes/*.json` depending on the 1.12.2 layout) content gets transcribed into `ModRecipes.java`
  rather than copied as files

---

## Cross-Phase Dependencies

```
Phase 1 (Scaffolding & Registration)
    ↓
Phase 2 (Framed Compact Drawer) ─┐
Phase 3 (Framed Controller)      ├─ can partially parallelize once Phase 1's registry stubs exist
Phase 4 (Framed Slave)          ─┘   (Phase 4's slave proxying references Phase 3's controller toggle API)
    ↓
Phase 5 (Rendering: base + overlay) ← depends on all three blocks/tiles having correct metadata & materials
    ↓
Phase 6 (Recipes & Assets) ← depends on final block/item set from Phases 2–4
    ↓
Phase 7 (Lifecycle Wiring & Cleanup)
```

Phase 1 must come first. Phases 2–4 can be worked in any order once Phase 1's `ModBlocks`/`ModItems` stubs exist,
though Phase 4 (slave) delegates to Phase 3 (controller)'s `toggle()` API, so implementing 3 before 4 avoids
rework. Phase 5 depends on all of 2–4 being in place (rendering reads material fields and metadata from every
tile). Phase 6 depends on the final block/item classes existing (recipes reference them directly). Phase 7 is
final integration and cleanup once everything above compiles and runs individually.

---

## Phase 7 — Lifecycle Wiring & Cleanup

**Goal:** All registration, recipe, and render setup fires from the correct FML lifecycle events; codebase is
clean of placeholders.

**Files to modify:**
- `FramedCompactDrawers.java` — confirm `@Mod` lifecycle methods dispatch to `proxy.preInit()`/`proxy.init()`
  at the right `FMLPreInitializationEvent`/`FMLInitializationEvent` stages
- `CommonProxy.java` — confirm full call order: block/item registration (Phase 1) → tile registration (Phases
  2–4) → recipes (Phase 6) all happen in `preInit()`/`init()` as appropriate
- `ClientProxy.java` — confirm render registration (Phase 5) happens in `init()`, after block/item registration
- `Config.java` — remove the placeholder "greeting" stub, or repurpose it for real config options if any surfaced
  during the port (e.g. a toggle for recipe registration)

**Verify:**
- Run `get_errors` (or equivalent static check) and iterate until clean
- Full `gradlew build` + dev client smoke test: place all three framed blocks, verify materials persist, verify
  recipes craft, verify localization strings display

---

## Out of Scope

`integration/waila/WailaPlugin.java` and related infrastructure (`IIntegrationPlugin`, `IntegrationRegistry`) are **excluded** from this backport per explicit instruction. May be added in a future phase.

**Also deferred (flag if they surface during implementation, don't block on them):**
- NEI hiding of intermediate "raw" item variants, if any exist — check after the core port compiles and runs;
  not addressed by any phase above.
- Confirm whether `TileEntity.registerTileEntity` name collisions matter when subclassing `TileEntityDrawersComp`,
  `TileEntityController`, `TileEntitySlave` — mitigated by using unique registration names throughout (see
  Resolved Questions below), but worth a sanity check once Phase 1 registration is in place.

---

## Verification (Per Phase)

Use the `verify-phase` skill after each phase:

1. **Compile check** — `gradlew build` with `spotlessApply` and `check`
2. **Dev client load** — mod loads without crash
3. **Behavior check** — in-game verification of the phase's specific functionality

---

## Phase Pacing

**Pause after each phase for testing.** Do not proceed to the next phase until the current phase's verification
steps (compile check, dev client load, behavior check) have been run and confirmed. This applies to all 7 phases
above.

---

## Resolved Questions

These were originally open questions; answered here based on findings from PLAN.md (written against the actual
decompiled StorageDrawers 1.7.10 reference and 1.12.2 FCD source):

1. **Rendering approach** — *Not* raw GL11 from scratch. 1.7.10 StorageDrawers ships its own procedural
   box-rendering helpers (`ModularBoxRenderer`, `PanelBoxRenderer`, `RenderHelper`/`RenderHelperState` in
   `com.jaquadro.minecraft.storagedrawers.client.renderer` / `.util`) that are the direct replacement for
   Chameleon's `ChamRender`/`PanelBoxRenderer`. Phase 5 ports the existing 1.12.2 panel geometry onto these
   helpers rather than writing new low-level rendering code.
2. **Material data storage** — Split by tile type, not uniform NBT-on-every-tile as originally assumed:
   - `TileFramedCompactDrawer` (extends `TileEntityDrawersComp` → `TileEntityDrawers`): **no custom fields
     needed** — `TileEntityDrawers` already has `getMaterialSide()/getMaterialFront()/getMaterialTrim()` +
     setters backed by `MatS`/`MatF`/`MatT`.
   - `TileFramedController` and `TileFramedSlave` (neither descends from `TileEntityDrawers`): **do** need custom
     `matSide`/`matTrim`/`matFront` `ItemStack` fields with manual NBT read/write, using the same `MatS`/`MatF`/
     `MatT` tag names for consistency.
3. **Phase pacing** — Resolved above: pause after each phase for testing before proceeding.
