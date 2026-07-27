package me.delta.client.module;

import me.delta.client.module.modules.combat.*;
import me.delta.client.module.modules.misc.*;
import me.delta.client.module.modules.movement.*;
import me.delta.client.module.modules.player.*;
import me.delta.client.module.modules.render.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public void initialize() {
        // Combat
        add(new KillAura());
        add(new Velocity());
        add(new Criticals());
        add(new AutoTotem());

        // Movement
        add(new Sprint());
        add(new Speed());
        add(new Flight());
        add(new NoFall());
        add(new Step());

        // Render
        add(new ESP());
        add(new FullBright());
        add(new Tracers());
        add(new Chams());
        add(new StorageESP());

        // Player
        add(new Scaffold());
        add(new NoSlow());
        add(new AntiVoid());
        add(new FastUse());

        // Misc
        add(new AntiHunger());
        add(new NoPacketKick());
        add(new AutoTool());
    }

    private void add(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .toList();
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> moduleClass) {
        return (T) modules.stream()
                .filter(m -> m.getClass() == moduleClass)
                .findFirst()
                .orElse(null);
    }

    public Module getModule(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Module> getEnabledModules() {
        return modules.stream()
                .filter(Module::isEnabled)
                .toList();
    }
}
