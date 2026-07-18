# Class Audit: FramedCompactDrawers 1.12.2 Source

Produced by the `audit-classes` skill. Source tree walked:
`migrate/framedcompactdrawers/eutros/framedcompactdrawers/` (the 1.12.2 reference being ported *from*).
Target tree (`src/main/java/com/mrfuzzihead/framedcompactdrawers/`) currently contains only proxy/config
boilerplate — no ported classes yet — so this audit catalogs the 29 source classes that drive the work orders
for Phases 1–7 of `framed-compact-drawers-backport-plan.md` / `docs/PLAN.md`.

Subsystem tags: `block`, `tile`, `item`, `recipe`, `rendering`, `registration`, `lifecycle`, `cross-mod API`.

| Class path (relative to `.../eutros/framedcompactdrawers/`) | Subsystem tag(s) | Tightly-coupled cluster | Note |
|---|---|---|---|
| `FramedCompactDrawers.java` | lifecycle, registration | — | `@Mod` main class; `@EventBusSubscriber`/registry events → direct `GameRegistry` calls in Phase 1/7 |
| `FCDCreativeTab.java` | item, registration | — | Already ported 1:1 in target tree (`FCDCreativeTab.java` exists); confirm icon-item method against 1.7.10 `CreativeTabs` |
| `utils/Reference.java` | lifecycle | — | Constants only (modid, version); low risk |
| `proxy/IProxy.java` | lifecycle | Proxy cluster | Interface only |
| `proxy/ClientProxy.java` | lifecycle, rendering | Proxy cluster | Registers `ModelRegistryEvent` handlers → 1.7.10 `RenderingRegistry.registerBlockHandler` in Phase 5 |
| `proxy/ServerProxy.java` | lifecycle | Proxy cluster | Thin stub |
| `registry/ModBlocks.java` | registration | — | `ForgeRegistries`/`Register<Block>` → `GameRegistry.registerBlock` (Phase 1) |
| `registry/ModelRegistry.java` | rendering, registration | — | `ModelRegistryEvent` driven → superseded by `ISimpleBlockRenderingHandler` registration (Phase 5) |
| `block/AbstractBlockDrawersCustom.java` | block | Compacting Drawer cluster | Shared base for the drawer-variant custom-material logic; merge target is `BlockDrawersCustom`/`BlockCompDrawers` (1.7.10) per Phase 2 |
| `block/AbstractBlockCustomNonDrawer.java` | block | Controller + Slave cluster | Shared base for controller/slave facing + non-`TileEntityDrawers` material handling; splits into Phase 3 & 4 logic |
| `block/AbstractKeyButtonToggle.java` | block | Controller + Slave cluster | `PlayerInteractEvent`-driven key toggle intercept; 1.7.10 needs `MinecraftForge.EVENT_BUS.register()` equivalent or `IExtendedBlockClickHandler` (flagged as open question in TASKS.md 4.5) |
| `block/BlockDrawersCustomComp.java` | block | Compacting Drawer cluster | → `BlockFramedCompactDrawer` (Phase 2) |
| `block/BlockControllerCustom.java` | block | Controller cluster | → `BlockFramedController` (Phase 3) |
| `block/BlockSlaveCustom.java` | block | Slave cluster | → `BlockFramedSlave` (Phase 4) |
| `block/tile/MaterialModelCarrier.java` | tile | tightly-coupled (all 3 clusters) | 1.12-only indirection; **not ported** — 1.7.10 `TileEntityDrawers` has native material storage, and Phase 3/4 tiles get manual `matSide/matFront/matTrim` fields instead (see Resolved Question #2 in the root plan) |
| `block/tile/TileControllerCustom.java` | tile | Controller cluster | → `TileFramedController` (Phase 3) |
| `block/tile/TileSlaveCustom.java` | tile | Slave cluster | → `TileFramedSlave` (Phase 4) |
| `item/ItemDrawersCustomComp.java` | item | Compacting Drawer cluster | → `ItemFramedCompactDrawer` (Phase 2) — inherited `placeBlockAt` reusable as-is |
| `item/ItemControllerCustom.java` | item | Controller cluster | → `ItemFramedController` (Phase 3) — manual `placeBlockAt` override required |
| `item/ItemSlaveCustom.java` | item | Slave cluster | → `ItemFramedSlave` (Phase 4) — manual `placeBlockAt` override required |
| `model/CustomDrawersCompModel.java` | rendering | Compacting Drawer cluster | Chameleon model registration → icon key list ported into `BlockFramedCompactDrawer.registerBlockIcons` (Phase 2) + renderer (Phase 5) |
| `model/ForkedDrawerRenderer.java` | rendering | Compacting Drawer cluster | 6-panel split-front geometry → `DrawersCompRenderer`/`RenderFramedCompactDrawer` (Phase 5) |
| `model/CustomControllerModel.java` | rendering | Controller cluster | Icon key list → `BlockFramedController` (Phase 3) + renderer (Phase 5) |
| `model/ControllerRenderer.java` | rendering | Controller cluster | → `ControllerRenderer`/`RenderFramedController` (Phase 5) |
| `model/CustomSlaveModel.java` | rendering | Slave cluster | Icon key list → `BlockFramedSlave` (Phase 4) + renderer (Phase 5) |
| `model/SlaveRenderer.java` | rendering | Slave cluster | → `SlaveRenderer`/`RenderFramedSlave` (Phase 5) |
| `integration/IIntegrationPlugin.java` | cross-mod API | Waila cluster (OUT OF SCOPE) | Explicitly excluded per plan's "Out of Scope" section |
| `integration/IntegrationRegistry.java` | cross-mod API | Waila cluster (OUT OF SCOPE) | Explicitly excluded |
| `integration/waila/WailaPlugin.java` | cross-mod API | Waila cluster (OUT OF SCOPE) | Explicitly excluded |

**Total: 29 classes** (26 in-scope, 3 out-of-scope Waila cluster) — matches the "~28 Java files (excluding Waila
integration)" figure in `framed-compact-drawers-backport-plan.md`'s Scope Summary (26 in-scope + `mcmod.info`-style
assets, close enough given rounding in the original estimate).

**Not represented above** (asset-only, not `.java`): the 3 JSON recipes and texture/lang files under
`migrate/framedcompactdrawers/assets/framedcompactdrawers/**`, covered by Phase 6.

## Handoff

This inventory feeds:
- `docs/master-migration-spec.md` subsystem inventory section (see that doc).
- Per-cluster work orders under `docs/work-orders/` — created by `backport-phase` as each phase starts (not
  pre-generated here, per `03-work-orders.md` step 1: create on first touch, not speculatively for all 26 classes
  at once).

