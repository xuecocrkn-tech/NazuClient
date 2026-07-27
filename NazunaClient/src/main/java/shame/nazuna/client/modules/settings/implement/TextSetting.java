package shame.nazuna.client.modules.settings.implement;

import java.util.function.Supplier;
import shame.nazuna.client.modules.settings.Setting;

public class TextSetting extends Setting {
    private String text;
    private final int maxLength;

    public TextSetting(String name, String text) {
        this(name, text, 32);
    }

    public TextSetting(String name, String text, int maxLength) {
        super(name);
        this.maxLength = Math.max(1, maxLength);
        setText(text);
    }

    public String getText() {
        return this.text;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public void setText(String text) {
        if (text == null) {
            this.text = "";
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length() && builder.length() < this.maxLength; i++) {
            char chr = text.charAt(i);
            if (!Character.isISOControl(chr)) {
                builder.append(chr);
            }
        }
        this.text = builder.toString();
    }

    public String get() {
        return this.text;
    }

    public TextSetting visible(Supplier<Boolean> state) {
        this.visible = state;
        return this;
    }
}
