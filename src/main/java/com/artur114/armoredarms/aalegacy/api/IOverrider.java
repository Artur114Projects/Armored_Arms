package com.artur114.armoredarms.aalegacy.api;


import com.artur114.armoredarms.aalegacy.api.events.InitArmorRenderLayerEvent;

/**
 * The base class from which the other overriders are inherited is not used directly!<br>
 * An Overrider is needed to rewrite the base rendering system for a specific item or mod.
 * You can register your Overrider in the event below.
 * @see InitArmorRenderLayerEvent
 */
public interface IOverrider {}
