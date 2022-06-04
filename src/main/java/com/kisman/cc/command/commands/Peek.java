package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import com.kisman.cc.gui.misc.PreviewGui;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Peek extends Command {
    private GuiScreen screen;

    public Peek() {
        super("peek");
    }

    public void runCommand(String s, String[] args) {
        try {
            if(!args[0].equalsIgnoreCase("book") || !args[0].equalsIgnoreCase("shulker")) {
                ChatUtils.error("Usage: " + getSyntax());

                return;
            }

            boolean book = (args.length > 0 && args[0].equals("book")) ? true : false;

            double distance = 0;
            ItemStack stack = null;
            for(Entity entity : mc.world.<Entity>getEntitiesWithinAABB(Entity.class, mc.player.getEntityBoundingBox().grow(12.0D, 4.0D, 12.0D))) {
                if(entity == mc.player) continue;

                ItemStack current = null;

                // Search through entities metadata
                for(EntityDataManager.DataEntry<?> entry : entity.getDataManager().getAll()) {
                    if(entry.getValue() instanceof ItemStack && ((book && (((ItemStack) entry.getValue()).getItem() instanceof ItemWritableBook || ((ItemStack) entry.getValue()).getItem() instanceof ItemWrittenBook)) || (!book && ((ItemStack) entry.getValue()).getItem() instanceof ItemShulkerBox))) {
                        current = (ItemStack) entry.getValue();
                        break;
                    }
                }

                if(current == null) { // Search through entity equipment
                    for(ItemStack item : entity.getEquipmentAndArmor()) {
                        if((!book && item.getItem() instanceof ItemShulkerBox) || (book && (item.getItem() instanceof ItemWritableBook || item.getItem() instanceof ItemWrittenBook))) {
                            current = item;
                            break;
                        }
                    }
                }

                double sqDist = mc.player.getDistanceSq(entity);
                if(current != null && (stack == null || sqDist < distance)) {
                    stack = current;
                    distance = sqDist;
                }
            }

            if(stack == null) {
                ChatUtils.error("No " + (book ? "book" : "shulker") + " item close to you");

                return;
            }

            if(book) {

                if(stack.getItem() instanceof ItemWritableBook) {
                    stack = stack.copy();
                    stack.getTagCompound().setString("title", "Writable book");
                    stack.getTagCompound().setString("author", "No author");
                }

                if(! stack.getTagCompound().hasKey("pages", 9)) {
                    ChatUtils.error("Book has no data");

                    return;
                }

                ChatUtils.message("Book size: " + Integer.toString(getItemSize(stack)) + " bytes");

                this.screen = new GuiScreenBook(mc.player, stack, false);
                MinecraftForge.EVENT_BUS.register(this);
            } else {
                NBTTagCompound tag = stack.getTagCompound();
                if(tag != null && tag.hasKey("BlockEntityTag") && tag.getTagId("BlockEntityTag") == 10) {
                    NBTTagCompound blockTag = tag.getCompoundTag("BlockEntityTag");
                    if(blockTag.hasKey("Items") && blockTag.getTagId("Items") == 9) {
                        this.screen = new PreviewGui(blockTag.getTagList("Items", 10), false);
                        MinecraftForge.EVENT_BUS.register(this);
                        return;
                    }
                }

                ChatUtils.error("Shulker is empty, or the server did not communicate its content");
            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    public String getDescription() {
        return "";
    }

    public String getSyntax() {
        return "peek <shulcer/book>";
    }

    @SubscribeEvent
    public void onGuiOpened(GuiOpenEvent event) {
        if(event.getGui() == null) {
            event.setGui(this.screen);
            this.screen = null;
        }
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    private int getItemSize(ItemStack stack) {
        PacketBuffer buff = new PacketBuffer(Unpooled.buffer());
        buff.writeItemStack(stack);
        int size = buff.writerIndex();
        buff.release();
        return size;
    }
}
