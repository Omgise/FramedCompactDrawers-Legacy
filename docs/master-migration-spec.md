# Master Migration Spec: FramedCompactDrawers 1.12.2 → 1.7.10

> Living doc, append/update-only (per `.clinerules/02-master-migration-spec.md`). Update phase status/DoD
> checkboxes as `verify-phase` passes each phase — do not regenerate this file wholesale.

## Scope

Backport **Framed Compact Drawers** (adds framed variants of 3 Storage Drawers blocks: Compacting Drawer,
Drawer Controller, Slave) from Minecraft 1.12.2 to 1.7.10, targeting package `com.mrfuzzihead.framedcompactdrawers`
(modid `framedcompactdrawers`). Storage Drawers itself is an existing dependency, not ported. Waila/Hwyla
integration is explicitly out of scope. Full detail lives in `../framed-compact-drawers-backport-plan.md`
(narrative plan) and `PLAN.md`/`TASKS.md` (granular checklist) — this doc is the high-level index over those.

## Source Build (1.12.2, port FROM)

- Minecraft: 1.12.2
- Mod version: 1.2.7 (per `migrate/framedcompactdrawers/mcmod.info`)
- Forge build: not pinned in this repo (source is a decompiled snapshot under `migrate/framedcompactdrawers/`,
  no build.gradle carried over) — **unspecified**, treat method names as needing compile-driven verification
  rather than an exact MCP mapping set lookup.
- MCP mapping set: not recorded — the 1.12.2 API delta rows in
  `.clinerules/01-api-delta-1.12.2-1.7.10.md` and the plan's own delta table use commonly-known SRG names
  (`func_xxxxx_x`) where identifiable, but per the standing rule these are **leads to verify**, not confirmed
  mappings (a few rows, e.g. the `World.func_175713_t`/`func_175704_b` entries in the root plan's delta table,
  could not be independently confirmed and should be spot-checked against a real 1.12.2 MCP export before being
  relied on).

## Target Build (1.7.10, port TO)

- Minecraft: `1.7.10` (`gradle.properties`)
- Forge: `10.13.4.1614` (`gradle.properties: forgeVersion`)
- MCP mappings: channel `stable`, version `12` (`gradle.properties: channel` / `mappingsVersion`)
- FML package namespace: `cpw.mods.fml.*` (pre-namespace-split)
- Key dependency: `com.github.GTNewHorizons:StorageDrawers:2.2.26-GTNH:dev` (`dependencies.gradle`) — decompiled
  reference classes for this jar live under `migrate/storagedrawers/**` for signature lookup only.

## Subsystem Inventory

Pulled from `docs/class-audit.md` (29 classes total, 26 in-scope + 3 out-of-scope Waila cluster):

| Subsystem | Class count (in-scope) | Clusters |
|---|---|---|
| block | 6 | Compacting Drawer, Controller, Slave |
| tile | 3 | Compacting Drawer (none needed — inherited), Controller, Slave |
| item | 3 | Compacting Drawer, Controller, Slave |
| rendering | 6 | Compacting Drawer, Controller, Slave |
| registration | 3 | Proxy/lifecycle |
| lifecycle | 5 | Proxy cluster + main mod class |
| cross-mod API (Waila) | 3 | **Out of scope** |

Recipes/assets (JSON + textures/lang, not `.java`) are tracked in Phase 6 directly, not in the class audit.

## Phase Order (locked)

Dependency-driven order, per the Cross-Phase Dependencies diagram in `../framed-compact-drawers-backport-plan.md`:

1. Scaffolding & Registration
2. Framed Compact Drawer *(can parallelize with 3, 4)*
3. Framed Drawer Controller *(can parallelize with 2, 4; 4 depends on this)*
4. Framed Slave *(depends on Phase 3's toggle API)*
5. Rendering *(depends on 2–4)*
6. Recipes & Assets *(depends on 2–4)*
7. Lifecycle Wiring & Cleanup *(final)*

## Definition of Done per Phase

Status legend: ⬜ Not started · 🟨 In progress · ✅ Verified (per `verify-phase`, logged in
`docs/verification-log.md`)

| Phase | Status | Definition of Done |
|---|---|---|
| 1 — Scaffolding & Registration | ⬜ | `docs/class-audit.md` + this doc exist; `ModBlocks`/`ModItems`/`FCDCreativeTab` compile-clean; `gradlew build` succeeds; dev client loads with mod in mod list |
| 2 — Framed Compact Drawer | ⬜ | `BlockFramedCompactDrawer`/`TileFramedCompactDrawer`/`ItemFramedCompactDrawer` compile; block places, stores materials, 3-slot behavior matches `BlockCompDrawers` |
| 3 — Framed Drawer Controller | ⬜ | `BlockFramedController`/`TileFramedController`/`ItemFramedController` compile; key-toggle items work; materials persist via NBT; drops preserve materials |
| 4 — Framed Slave | ⬜ | `BlockFramedSlave`/`TileFramedSlave`/`ItemFramedSlave` compile; toggle proxying to bound controller (vanilla or framed) works |
| 5 — Rendering | ⬜ | All 3 blocks render base + overlay passes with correct material textures in world and inventory |
| 6 — Recipes & Assets | ⬜ | All 3 recipes craft correct output; textures/lang/mcmod.info finalized, no missing-texture purple/black |
| 7 — Lifecycle Wiring & Cleanup | ⬜ | Full call-order audit clean; `get_errors` clean; full `gradlew build` + manual smoke test of all 3 blocks passes |

## Process Notes

- **Class audit**: `docs/class-audit.md` (completed for this backport's full scope up front, since the 1.12.2
  source tree is fixed and small — re-run only if `migrate/framedcompactdrawers/` changes after an upstream pull).
- **Work orders**: created per-cluster under `docs/work-orders/<cluster>.md` by `backport-phase` as each phase
  starts, not pre-generated for all phases here (per `.clinerules/03-work-orders.md`).
- **Verification log**: `docs/verification-log.md` — `verify-phase` records pass/fail per check there per phase.
- **Duplicate plan docs**: `../framed-compact-drawers-backport-plan.md` and `PLAN.md`/`TASKS.md` describe the same
  phase breakdown from two different drafting passes; they are substantively consistent (both correctly reflect
  that `TileEntityDrawers` has native material storage but `TileEntityController`/`TileEntitySlave` do not). This
  doc is now the canonical status tracker — treat the other two as detail/checklist references, and if they ever
  disagree on a technical point, this doc's Subsystem Inventory + Phase Order win, with the disagreement flagged
  here for reconciliation.

