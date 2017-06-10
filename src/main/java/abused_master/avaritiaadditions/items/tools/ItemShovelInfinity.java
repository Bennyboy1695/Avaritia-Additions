package abused_master.avaritiaadditions.items.tools;

import abused_master.avaritiaadditions.AvaritiaAdditions;
import abused_master.avaritiaadditions.items.entity.EntityImmortalItem;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemShovelInfinity extends ItemSpade {

    public static final ToolMaterial opShovel = EnumHelper.addToolMaterial("INFINITY_SHOVEL", 32, 9999, 9999F, 7.0F, 200);
    public static EnumRarity cosmic = EnumHelper.addRarity("COSMIC", TextFormatting.RED, "Cosmic");
    boolean destroyer = false;

    public ItemShovelInfinity(){
        super(opShovel);
        setUnlocalizedName("infinity_shovel");
        setCreativeTab(AvaritiaAdditions.AvaritiaAdditions);
        GameRegistry.register(this.setRegistryName("infinity_shovel"));
    }

    @Override
    public void setDamage(ItemStack stack, int damage){
        super.setDamage(stack, 0);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return cosmic;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        return Math.max(super.getStrVsBlock(stack, state), 1.0F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if(player.isSneaking()) {
            NBTTagCompound tags = stack.getTagCompound();
            if (tags == null) {
                tags = new NBTTagCompound();
                stack.setTagCompound(tags);
            }
            tags.setBoolean("destroyer", !tags.getBoolean("destroyer"));
            if (tags.getBoolean("destroyer")) {
                player.addChatMessage(new TextComponentString(TextFormatting.GREEN + "Changed mode of tool to Destroyer!"));
            } else {
                player.addChatMessage(new TextComponentString(TextFormatting.GREEN + "Changed mode of tool to Shovel"));
            }
            player.swingArm(hand);
            if(destroyer) {
                destroyer = false;
            }else {
                destroyer = true;
            }
        }
        return super.onItemRightClick(stack, world, player, hand);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if(stack.getTagCompound() != null && stack.getTagCompound().getBoolean("destroyer")) {
            RayTraceResult raycast = ToolHelper.raytraceFromEntity(player.worldObj, player, true, 10);
            if (raycast != null) {
                breakOtherBlock(player, stack, pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ(), raycast.sideHit.getHorizontalIndex());
            }
        }
        return false;
    }

    public void breakOtherBlock(EntityPlayer player, ItemStack stack, int x, int y, int z, int originX, int originY, int originZ, int side) {

        World world = player.worldObj;
        Material mat = world.getBlockState(new BlockPos(x, y, z)).getMaterial();
        if(!ToolHelper.isRightMaterial(mat, ItemPickaxeInfinity.MATERIALS))
            return;

        if(world.isAirBlock(new BlockPos(x, y, z)))
            return;

        EnumFacing direction = EnumFacing.getHorizontal(side);
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(35), stack);
        int silktouch = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(33), stack);
        boolean silk;
        if(silktouch > 0) {
            silk = true;
        } else {
            silk = false;
        }
        boolean doY = direction.getFrontOffsetY() == 0;

        int range = 8;

        ToolHelper.removeBlocksInIteration(player, stack, world, x, y, z, -range, doY ? -1 : -range, -range, range, doY ? range * 2 - 2 : range, range, null, ItemPickaxeInfinity.MATERIALS, silk, fortune, false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return false;
    }
}
