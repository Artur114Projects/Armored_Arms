import com.artur114.armoredarms.client.util.ShapelessRL;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Test {
    public static Set<ResourceLocation> renderBlackList = new HashSet<>();
    public static void main(String[] args) {
        renderBlackList.add(new ShapelessRL("minecraft:*"));

        System.out.println(renderBlackList.contains(new ShapelessRL(new ResourceLocation("minecraft:air"))));
    }
}
