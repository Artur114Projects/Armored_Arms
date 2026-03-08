import com.artur114.armoredarms.core.api.*;
        import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.RenderEngines;

public class Test {
    public static void main(String[] args) {
        TestPipeline testPipeline = new TestPipeline();
        RenderEngines.registerEngine(new TestEngine());

        IArmRenderEngine<TestPipeline> engine = RenderEngines.pickUp(null, testPipeline.clazz());
        System.out.println(engine);
        if (engine != null) {
            engine.init(testPipeline, null);
        }
    }

    private static class TestEngine implements IArmRenderEngine<IArmRenderPipeline<?>> {

        @Override
        public void init(IArmRenderPipeline<?> context, IAAModContainer mod) {
            System.out.println(context);
        }

        @Override
        public void tryRender(IArmRenderPipeline<?> context) {

        }

        @Override
        public void tryTick(IArmRenderPipeline<?> context) {

        }

        @Override
        public boolean canWork(IAAModContainer mod) {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<IArmRenderPipeline<?>> targetPipeline() {
            return (Class<IArmRenderPipeline<?>>) (Class<?>) IArmRenderPipeline.class;
        }

        @Override
        public IPriority priority() {
            return null;
        }
    }

    private static class TestPipeline implements IArmRenderPipeline<TestPipeline> {

        @Override
        public void registerPipeline(IAAModContainer mod, IArmRenderEngine<TestPipeline> engine) {

        }

        @Override
        public boolean canWork(IAAModContainer mod) {
            return true;
        }

        @Override
        public IArmRenderEngine<TestPipeline> engine() {
            return null;
        }

        @Override
        public Class<TestPipeline> clazz() {
            return TestPipeline.class;
        }

        @Override
        public IPriority priority() {
            return null;
        }
    }
}
