package shame.nazuna.api.storages.implement;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.Formatting;
import shame.nazuna.NazunaClient;
import shame.nazuna.api.QClient;
import shame.nazuna.api.events.EventInvoker;
import shame.nazuna.api.events.EventLink;
import shame.nazuna.api.events.implement.EventBinding;
import shame.nazuna.api.utils.chat.ChatUtils;
import shame.nazuna.api.utils.cmd.macro.Macro;

public class MacroStorage implements QClient {
    private final List<Macro> macros;
    private final List<String> names;

    public MacroStorage() {
        this.macros = new ArrayList<>();
        this.names = new ArrayList<>();
    }

    public List<Macro> getMacros() { return this.macros; }

    public void add(Macro macro) {
        if (macro == null || macro.getName() == null || macro.getName().isBlank() || getMacro(macro.getName()) != null) {
            return;
        }
        this.macros.add(macro);
        this.names.add(macro.getName());
    }

    public void remove(Macro macro) {
        if (macro == null) {
            return;
        }
        this.macros.remove(macro);
        this.names.remove(macro.getName());
    }

    public void clear() {
        if (!this.macros.isEmpty()) this.macros.clear();
        if (!this.names.isEmpty()) this.names.clear();
    }

    public boolean isEmpty() {
        return this.macros.isEmpty();
    }

    public Macro getMacro(String name) {
        for (Macro macro : this.macros) {
            if (!macro.getName().equalsIgnoreCase(name))
                continue;
            return macro;
        }
        return null;
    }

    @EventLink
    public void onKey(EventBinding e) {
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1755 != null || mc.field_1724.field_3944 == null || this.macros.isEmpty())
            return;
        for (Macro macro : this.macros) {
            if (macro == null || macro.getBind() == null || macro.getBind().getKey() != e.getKey()) {
                continue;
            }
            executeMacro(macro);
        }
    }

    private void executeMacro(Macro macro) {
        String command = macro.getCommand();
        if (command == null || command.isBlank()) {
            return;
        }

        if (command.startsWith("/")) {
            mc.field_1724.field_3944.method_45730(command.substring(1));
            return;
        }
        String prefix = NazunaClient.INSTANCE.commandStorage.getPrefix();
        if (prefix != null && !prefix.isEmpty() && command.startsWith(prefix)) {
            try {
                NazunaClient.INSTANCE.commandStorage.getDispatcher().execute(command
                    .substring(prefix.length()), NazunaClient.INSTANCE.commandStorage
                    .getSource());
            } catch (CommandSyntaxException ignored) {
                ChatUtils.sendMessage(String.valueOf(Formatting.field_1061) + "Ошибка в использовании макроса " + String.valueOf(Formatting.field_1061) + "!");
            }
            return;
        }
        mc.field_1724.field_3944.method_45729(command);
    }
}
