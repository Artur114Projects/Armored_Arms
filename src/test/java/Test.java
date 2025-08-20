import com.artur114.armoredarms.client.util.ShapelessRL;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test {
    public static List<ResourceLocation> renderBlackList = new ArrayList<>();
    public static void main(String[] args) {
        renderBlackList.add(new ShapelessRL("minecraft:*"));
        renderBlackList.add(new ShapelessRL("*:aaa"));

        System.out.println(renderBlackList.contains(new ShapelessRL(new ResourceLocation("minecraft:air"))));
        System.out.println(renderBlackList.contains(new ShapelessRL(new ResourceLocation("sfgsfgf:aaa"))));
        System.out.println(renderBlackList.contains(new ShapelessRL(new ResourceLocation("sfgsfgf:eee"))));

        renderBlackList.clear();

        renderBlackList.add(new ShapelessRL("*:*"));

        System.out.println(renderBlackList.contains(new ShapelessRL(new ResourceLocation("sfgsfgf:wfwfefw"))));
    }
}
