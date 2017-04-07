package abused_master.avaritiaadditions.items.entity;

import java.lang.reflect.Field;
import java.util.Random;

import abused_master.avaritiaadditions.render.Text;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityHeavenArrow extends EntityArrow {
    public static Field inGroundField;
    public static Field ticksInGroundField;
    static {
        try {
            inGroundField = ReflectionHelper.findField(EntityArrow.class, "inGround", "field_70254_i");
            ticksInGroundField = ReflectionHelper.findField(EntityArrow.class, "ticksInGround", "field_70252_j");
        }
        catch(Exception e){
            Text.log(Level.ERROR, e);
        }
    }

    public boolean impacted = false;


    public EntityHeavenArrow(World world, double x,	double y, double z) {
        super(world, x, y, z);
    }

    public EntityHeavenArrow(World world, EntityLivingBase entity) {
        super(world, entity);

    }

    public EntityHeavenArrow(World world) {
        super(world);
    }

    @Override
    public void onUpdate() {
        this.rotationPitch = 0;
        this.rotationYaw = 0;
        super.onUpdate();
        if (!this.impacted) {
            try {
                if (inGroundField.getBoolean(this)) {
                    this.impacted = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (this.impacted) {
                if (!this.worldObj.isRemote) {
                    this.barrage();
                }
            }
        }

        if (getInGround(this) && getTicksInGround(this) >= 100) {
            this.setDead();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);
        tag.setBoolean("impacted", this.impacted);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);
        this.impacted = tag.getBoolean("impacted");
    }

    public static boolean getInGround(EntityArrow arrow) {
        try {
            return inGroundField.getBoolean(arrow);
        } catch (Exception e) {
            return false;
        }
    }

    public static int getTicksInGround(EntityArrow arrow) {
        try {
            return ticksInGroundField.getInt(arrow);
        } catch (Exception e) {
            return 0;
        }
    }

    public void barrage() {
        Random randy = getEntityWorld().rand;
        for (int i=0; i<10; i++) {
            double angle = randy.nextDouble() * 2 * Math.PI;
            double dist = randy.nextGaussian()*0.5;

            double x = Math.sin(angle) * dist + this.posX;
            double z = Math.cos(angle) * dist + this.posZ;
            double y = this.posY + 25.0;

            double dangle = randy.nextDouble() * 2 * Math.PI;
            double ddist = randy.nextDouble()*0.35;
            double dx = Math.sin(dangle) * ddist;
            double dz = Math.cos(dangle) * ddist;

            EntityArrow arrow = new EntityHeavenSubArrow(this.worldObj, x,y,z);
            arrow.shootingEntity = this.shootingEntity;
            arrow.addVelocity(dx, -(randy.nextDouble()*1.85 + 0.15), dz);
            arrow.setDamage(this.getDamage());
            arrow.setIsCritical(true);

            this.worldObj.spawnEntityInWorld(arrow);
        }
    }
    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(Items.ARROW);//TODO Replace with Item
    }
}