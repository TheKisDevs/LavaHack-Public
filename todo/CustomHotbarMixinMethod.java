@Shadow @Final public Minecraft mc;
@Shadow protected void renderHotbarItem(int p_184044_1_, int p_184044_2_, float p_184044_3_, EntityPlayer player, ItemStack stack) {}

/**
 * @author _kisman_
 */
@Overwrite
protected void renderHotbar1(ScaledResolution sr, float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
        if(!CustomHotbar.instance.isToggled()) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
        EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
        ItemStack itemstack = entityplayer.getHeldItemOffhand();
        EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
        int i = sr.getScaledWidth() / 2;
        float f = this.zLevel;
        this.zLevel = -90.0F;
        this.drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
        this.drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
        if (!itemstack.isEmpty()) {
        if (enumhandside == EnumHandSide.LEFT) {
        this.drawTexturedModalRect(i - 91 - 29, sr.getScaledHeight() - 23, 24, 22, 29, 24);
        } else {
        this.drawTexturedModalRect(i + 91, sr.getScaledHeight() - 23, 53, 22, 29, 24);
        }
        }

        this.zLevel = f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.enableGUIStandardItemLighting();

        int l1;
        int i2;
        int j2;
        for (l1 = 0; l1 < 9; ++l1) {
        i2 = i - 90 + l1 * 20 + 2;
        j2 = sr.getScaledHeight() - 16 - 3;
        this.renderHotbarItem(i2, j2, partialTicks, entityplayer, (ItemStack) entityplayer.inventory.mainInventory.get(l1));
        }

        if (!itemstack.isEmpty()) {
        l1 = sr.getScaledHeight() - 16 - 3;
        if (enumhandside == EnumHandSide.LEFT) {
        this.renderHotbarItem(i - 91 - 26, l1, partialTicks, entityplayer, itemstack);
        } else {
        this.renderHotbarItem(i + 91 + 10, l1, partialTicks, entityplayer, itemstack);
        }
        }

        if (this.mc.gameSettings.attackIndicator == 2) {
        float f1 = this.mc.player.getCooledAttackStrength(0.0F);
        if (f1 < 1.0F) {
        i2 = sr.getScaledHeight() - 20;
        j2 = i + 91 + 6;
        if (enumhandside == EnumHandSide.RIGHT) {
        j2 = i - 91 - 22;
        }

        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        int k1 = (int) (f1 * 19.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
        this.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
        }
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        } else {
        int x = CustomHotbar.hotbarX, y = CustomHotbar.hotbarY;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
        EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
        ItemStack itemstack = entityplayer.getHeldItemOffhand();
        EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
        int i = sr.getScaledWidth() / 2;
        float f = this.zLevel;
        this.zLevel = -90.0F;
        this.drawTexturedModalRect(x, y, 0, 0, 182, 22);
        this.drawTexturedModalRect(x - 1 + entityplayer.inventory.currentItem * 20, y - 1, 0, 22, 24, 22);
        if (!itemstack.isEmpty()) {
        this.drawTexturedModalRect(x + 182 + 9, y - 1, 53, 22, 29, 24);
        }

        this.zLevel = f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.enableGUIStandardItemLighting();

        int l1;
        int i2;
        int j2;
        for (l1 = 0; l1 < 9; ++l1) {
        i2 = x + l1 * 20 + 2;
        this.renderHotbarItem(i2, y + 3, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(l1));
        }

        if (!itemstack.isEmpty()) this.renderHotbarItem(x + 182 + 11, y + 3, partialTicks, entityplayer, itemstack);

        if (this.mc.gameSettings.attackIndicator == 2) {
        float f1 = this.mc.player.getCooledAttackStrength(0.0F);
        if (f1 < 1.0F) {
        i2 = sr.getScaledHeight() - 20;
        j2 = i + 91 + 6;
        if (enumhandside == EnumHandSide.RIGHT) {
        j2 = i - 91 - 22;
        }

        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        int k1 = (int) (f1 * 19.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
        this.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
        }
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        }
        }
        }