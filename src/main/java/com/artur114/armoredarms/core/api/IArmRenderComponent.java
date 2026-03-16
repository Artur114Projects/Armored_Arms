package com.artur114.armoredarms.core.api;

// TODO : Сделать is deactivate и чтоб деактивированные компоненты обрабатывались
public interface IArmRenderComponent {
    void deactivate();
    String type();
}
