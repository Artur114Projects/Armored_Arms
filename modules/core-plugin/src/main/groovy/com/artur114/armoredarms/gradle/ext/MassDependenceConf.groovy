package com.artur114.armoredarms.gradle.ext

class MassDependenceConf {
    public static final List<String> DEPENDENCE_LOAD_TYPES  = ["SEPARATED", "ALL_FLAT_DIR", "ALL_FILE_TREE"]
    private Closure<Object> deObfHook
    private String dependenceLoadType
    private Set<String> sources
    String configurationName

    MassDependenceConf() {
        this.configurationName = "implementation"
        this.dependenceLoadType = "SEPARATED"
        this.sources = new HashSet<>()
        this.deObfHook = {it}
    }

    Closure<Object> getDeObfHook() { this.deObfHook }

    String getDependenceLoadType() { this.dependenceLoadType }

    Set<String> getSources() { Collections.unmodifiableSet(this.sources) }

    void setDependenceLoadType(String type) {
        if (DEPENDENCE_LOAD_TYPES.contains(type)) {
            this.dependenceLoadType = type
        }
    }

    void deObfHook(Closure<?> closure) {
        this.deObfHook = closure as Closure<Object>
    }

    void source(String... source) {
        this.sources.addAll(Arrays.asList(source))
    }
}
