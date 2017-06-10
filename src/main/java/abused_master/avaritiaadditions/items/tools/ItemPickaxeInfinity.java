package abused_master.avaritiaadditions.items.tools;

import abused_master.avaritiaadditions.AvaritiaAdditions;
import abused_master.avaritiaadditions.items.entity.EntityImmortalItem;
import abused_master.avaritiaadditions.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemPickaxeInfinity extends ItemPickaxe {

    private static final ToolMaterial opPickaxe = EnumHelper.addToolMaterial("INFINITY_PICKAXE", 32, 9999, 9999F, 6.0F, 200);
    public static EnumRarity cosmic = EnumHelper.addRarity("COSMIC", TextFormatting.RED, "Cosmic");

    public static final Material[] MATERIALS = new Material[] { Material.ROCK, Material.IRON, Material.ICE, Material.GLASS, Material.PISTON, Material.ANVIL, Material.GRASS, Material.GROUND, Material.SAND, Material.SNOW, Material.CRAFTED_SNOW, Material.CLAY };

    public ItemPickaxeInfinity(){
        super(opPickaxe);
        setUnlocalizedName("infinity_pickaxe");
        setCreativeTab(AvaritiaAdditions.AvaritiaAdditions);
        GameRegistry.register(this.setRegistryName("infinity_pickaxe"));
    }

    @Override
    public void setDamage(ItemStack stack, int damage){
        super.setDamage(stack, 0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        ItemStack pick = new ItemStack(this);
        pick.addEnchantment(Enchantment.getEnchantmentByID(35), 10);
        list.add(pick);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return cosmic;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if(player.isSneaking()) {
            NBTTagCompound tags = stack.getTagCompound();
            if (tags == null) {
                tags = new NBTTagCompound();
                stack.setTagCompound(tags);
            }
            if(EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(35), stack) < 10)
                stack.addEnchantment(Enchantment.getEnchantmentByID(35), 10);
            tags.setBoolean("hammer", !tags.getBoolean("hammer"));
            if (tags.getBoolean("hammer")) {
                player.addChatMessage(new TextComponentString(TextFormatting.GREEN + "Changed mode of tool to Hammer!"));
            } else {
                player.addChatMessage(new TextComponentString(TextFormatting.GREEN + "Changed mode of tool to Pickaxe!"));
            }
            player.swingArm(hand);
        }
        return super.onItemRightClick(stack, world, player, hand);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase victim, EntityLivingBase player){
        if(stack.getTagCompound() != null){
            if(stack.getTagCompound().getBoolean("hammer")) {
                if(!(victim instanceof EntityPlayer && ModItems.isInfinite((EntityPlayer)victim))) {
                    int i = 10;
                    victim.addVelocity((double) (-MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F), 2.0D, (double) (MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F));
                }
            }
        }
        return true;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if(stack.getTagCompound() != null && stack.getTagCompound().getBoolean("hammer")) {
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
        if(!ToolHelper.isRightMaterial(mat, MATERIALS))
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

        ToolHelper.removeBlocksInIteration(player, stack, world, x, y, z, -range, doY ? -1 : -range, -range, range, doY ? range * 2 - 2 : range, range, null, MATERIALS, silk, fortune, false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        return (state.getBlock() == Blocks.BEDROCK) ? Float.MAX_VALUE : super.getStrVsBlock(stack, state);
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        return (blockIn.getBlock() == Blocks.BEDROCK) ? true : true;
    }
}
