if(mc.player == null || mc.world == null) return;
        if(!mc.world.getBlockState(mc.player.getPosition().up().up()).getBlock().equals(Blocks.AIR)) {
        state = State.None;
        if(disableOnComplete.getValBoolean()) {
        ChatUtils.complete("[SelfTrap] Complete!");
        super.setToggled(false);
        return;
        }
        } else state = State.Calc;
        if(state.equals(State.Calc)) {
        positions.clear();
        BlockPos posToPlace = mc.player.getPosition().up().up();
        if (mc.world.getBlockState(posToPlace).getBlock().equals(Blocks.AIR)) {
        Direction dir = debugMode.getValBoolean() ? ((Direction) new EnumUtil<Direction>().getEnumByName(primarySupportDirection.getValString())) : Direction.byName(primarySupportDirection.getValString());
        if (dir == null) {
        ChatUtils.error("Error! EnumUtil is not working!");
        super.setToggled(false);
        return;
        }

        BlockPos supportHeadPos = null;

        for (Vec3i vec : SelfTrapVectors.Around.sort(dir))
        if (!mc.world.getBlockState(posToPlace.add(vec)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(posToPlace.add(vec)).getMaterial().isLiquid()) {
        if(!BlockUtil2.isPositionPlaceable(posToPlace.add(vec), sideCheck.getValBoolean(), entityCheck.getValBoolean(), false)) continue;
        positions.add(posToPlace.add(vec));
        supportHeadPos = posToPlace.add(vec);
        break;
        }

        if (supportHeadPos == null) {
        BlockPos supportSupportHeadPos = null;

        for (Vec3i vec : SelfTrapVectors.Around.sort(dir))
        if (!mc.world.getBlockState(posToPlace.add(vec).down()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(posToPlace.add(vec).down()).getMaterial().isLiquid()) {
        if(!BlockUtil2.isPositionPlaceable(posToPlace.add(vec).down(), sideCheck.getValBoolean(), entityCheck.getValBoolean(), false)) continue;
        positions.add(posToPlace.add(vec).down());
        positions.add(posToPlace.add(vec));
        supportSupportHeadPos = posToPlace.add(vec).down();
        break;
        }

        if (supportSupportHeadPos == null) {
        BlockPos supportSupportSupportHeadPos = null;

        for (Vec3i vec : SelfTrapVectors.Around.sort(dir))
        if (!mc.world.getBlockState(posToPlace.add(vec).down().down()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(posToPlace.add(vec).down().down()).getMaterial().isLiquid()) {
        if(!BlockUtil2.isPositionPlaceable(posToPlace.add(vec).down().down(), sideCheck.getValBoolean(), entityCheck.getValBoolean(), false)) continue;
        positions.add(posToPlace.add(vec).down().down());
        positions.add(posToPlace.add(vec).down());
        positions.add(posToPlace.add(vec));
        supportSupportSupportHeadPos = posToPlace.add(vec).down().down();
        break;
        }

        if (supportSupportSupportHeadPos == null) {
        BlockPos supportSupportSupportSupportHeadPos = null;

        for (Vec3i vec : SelfTrapVectors.Around.sort(dir))
        if (!mc.world.getBlockState(posToPlace.add(vec).down().down().down()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(posToPlace.add(vec).down().down().down()).getMaterial().isLiquid()) {
        if(!BlockUtil2.isPositionPlaceable(posToPlace.add(vec).down().down().down(), sideCheck.getValBoolean(), entityCheck.getValBoolean(), false)) continue;
        positions.add(posToPlace.add(vec).down().down().down());
        positions.add(posToPlace.add(vec).down().down());
        positions.add(posToPlace.add(vec).down());
        positions.add(posToPlace.add(vec));
        supportSupportSupportSupportHeadPos = posToPlace.add(vec).down().down().down();
        break;
        }

        if (supportSupportSupportSupportHeadPos == null) {
        ChatUtils.error("[SelfTrap] can not find valid blocks! Disable!");
        super.setToggled(false);
        return;
        }
        }
        }
        }

        state = State.Place;
        }
        } else if(state.equals(State.Place)) {
        if(positions.isEmpty()) {
        ChatUtils.error("[SelfTrap] Need place positions! Disable!");
        super.setToggled(false);
        return;
        }

        for(BlockPos pos : positions) {
        place(pos);
        }
        }