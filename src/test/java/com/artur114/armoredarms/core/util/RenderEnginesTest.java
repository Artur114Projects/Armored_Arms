package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.*;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class RenderEnginesTest {
    @Test
    void pickUp() {
        TestPipeline pipeline = new TestPipeline();
        TestPipeline1 pipeline1 = new TestPipeline1();

        TestEngineAny engineAny = new TestEngineAny();
        TestEnginePip enginePip = new TestEnginePip();

        RenderEngines.registerEngine(engineAny);
        RenderEngines.registerEngine(enginePip);

        assertEquals(engineAny, RenderEngines.pickUp(null, pipeline.clazz()));
        assertEquals(enginePip, RenderEngines.pickUp(null, pipeline1.clazz()));

        Method method = ReflectionUtils.findMethod(RenderEngines.class, "clear").orElse(null);

        assertNotNull(method);

        ReflectionUtils.invokeMethod(method, null);

        RenderEngines.registerEngine(enginePip);

        assertNull(RenderEngines.pickUp(null, pipeline.clazz()));
        assertNotNull(RenderEngines.pickUp(null, pipeline1.clazz()));
    }

    private static class TestEngineAny implements IArmRenderEngine<IArmRenderPipeline<?>> {

        @Override
        public void init(IArmRenderPipeline<?> context, IAAModContainer mod) {
            assertNotNull(context);
        }

        @Override
        public void tryRender(IArmRenderPipeline<?> context) {}

        @Override
        public void tryTick(IArmRenderPipeline<?> context) {}

        @Override
        public <L extends IArmRenderLayer<?>> L layer(Class<L> clazz) {
            return null;
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
            return Priority.NORMAL;
        }

        @Override
        public void deactivate() {

        }
    }

    private static class TestEnginePip implements IArmRenderEngine<TestPipeline1> {

        @Override
        public void init(TestPipeline1 context, IAAModContainer mod) {
            assertNotNull(context);
        }

        @Override
        public void tryRender(TestPipeline1 context) {}

        @Override
        public void tryTick(TestPipeline1 context) {}

        @Override
        public <L extends IArmRenderLayer<?>> L layer(Class<L> clazz) {
            return null;
        }

        @Override
        public boolean canWork(IAAModContainer mod) {
            return true;
        }

        @Override
        public Class<TestPipeline1> targetPipeline() {
            return TestPipeline1.class;
        }

        @Override
        public IPriority priority() {
            return Priority.HIGH;
        }

        @Override
        public void deactivate() {

        }
    }

    private static class TestPipeline implements IArmRenderPipeline<TestPipeline> {
        @Override
        public IPriority priority() {
            return Priority.NORMAL;
        }

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
        public void deactivate() {

        }
    }

    private static class TestPipeline1 implements IArmRenderPipeline<TestPipeline1> {
        @Override
        public IPriority priority() {
            return Priority.NORMAL;
        }

        @Override
        public void registerPipeline(IAAModContainer mod, IArmRenderEngine<TestPipeline1> engine) {

        }

        @Override
        public boolean canWork(IAAModContainer mod) {
            return true;
        }

        @Override
        public IArmRenderEngine<TestPipeline1> engine() {
            return null;
        }

        @Override
        public Class<TestPipeline1> clazz() {
            return TestPipeline1.class;
        }

        @Override
        public void deactivate() {

        }
    }

}