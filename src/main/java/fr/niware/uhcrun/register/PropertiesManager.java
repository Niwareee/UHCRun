package fr.lifecraft.uhcrun.register;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.hook.PotionAttackDamageNerf;
import fr.lifecraft.uhcrun.hook.Reflection;
import fr.lifecraft.uhcrun.hook.SlotPatcher;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import net.minecraft.server.v1_8_R3.MobEffectList;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Map;

public class PropertiesManager {

    public static void enablePatch() {

        Main main = Main.getInstance();

        // PATCH SLOT

        try {
            SlotPatcher.changeSlots(main.getGame().getSlot());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        // PATCH POTIONS

        try {
            Reflection.setFinalStatic(PotionEffectType.class.getDeclaredField("acceptingNew"), true);

            Field byIdField = Reflection.getField(PotionEffectType.class, true, "byId");
            Field byNameField = Reflection.getField(PotionEffectType.class, true, "byName");
            ((Map<?, ?>) byNameField.get(null)).remove("increase_damage");
            ((PotionEffectType[]) byIdField.get(null))[5] = null;
            main.log("§6Patching Strength Potion (130% => 43.3%, 260% => 86.6%)");
            Reflection.setFinalStatic(MobEffectList.class.getDeclaredField("INCREASE_DAMAGE"), (new PotionAttackDamageNerf(5, new MinecraftKey("strength"), false, 9643043)).c("potion.damageBoost").a(GenericAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 2.5D, 2));
            main.log("§6Potions patched !");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}