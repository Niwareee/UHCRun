package fr.lifecraft.uhcrun.manager;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.hook.PotionAttackDamageNerf;
import fr.lifecraft.uhcrun.hook.Reflection;
import fr.lifecraft.uhcrun.hook.SlotPatcher;
import fr.lifecraft.uhcrun.listeners.WorldEvent;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import net.minecraft.server.v1_8_R3.MobEffectList;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Map;

public class PropertiesManager {

    private final Main main;

    public PropertiesManager(Main main) {
        this.main = main;

        enableSlotPatch();

        try {
            patchPotions();

        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void enableSlotPatch() {
        try {
            new SlotPatcher().changeSlots(main.getGame().getSlot());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        new SlotPatcher().updateServerProperties();
    }

    public void patchPotions() throws ReflectiveOperationException {
        Reflection.setFinalStatic(PotionEffectType.class.getDeclaredField("acceptingNew"), true);

        Field byIdField = Reflection.getField(PotionEffectType.class, true, "byId");
        Field byNameField = Reflection.getField(PotionEffectType.class, true, "byName");
        ((Map<?, ?>) byNameField.get(null)).remove("increase_damage");
        ((PotionEffectType[]) byIdField.get(null))[5] = null;
        main.log("§6Patching Strength Potion (130% => 43.3%, 260% => 86.6%)");
        Reflection.setFinalStatic(MobEffectList.class.getDeclaredField("INCREASE_DAMAGE"),
                (new PotionAttackDamageNerf(5, new MinecraftKey("strength"), false, 9643043)).c("potion.damageBoost")
                        .a(GenericAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 2.5D, 2));
        main.log("§6Potions patched !");
    }
}
