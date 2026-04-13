package com.artur114.armoredarms.main;

import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.AbstractLoggingManager;
import com.artur114.armoredarms.core.util.ITextMessage;
import com.artur114.armoredarms.core.util.MessageFormating;
import com.artur114.armoredarms.core.util.ShapelessLocationMap;

public class LoggingManager extends AbstractLoggingManager {
    @Override
    protected String splitter() {
        return "";
    }

    @Override
    protected String expMessageKey() {
        return "";
    }

    @Override
    protected IArmRenderPipeline<?> pipeline() {
        return null;
    }

    @Override
    protected String formatingToString(MessageFormating formating) {
        return "";
    }

    @Override
    protected String localise(String translateKey, Object... parameters) {
        return "";
    }

    @Override
    protected ITextMessage createMessageLiteral(String message) {
        return null;
    }

    @Override
    protected ITextMessage createMessageTranslate(String key) {
        return null;
    }

    @Override
    protected ShapelessLocationMap<String> initLocaliseMap() {
        return null;
    }
}
