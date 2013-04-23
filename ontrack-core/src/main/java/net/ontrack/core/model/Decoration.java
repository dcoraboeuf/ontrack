package net.ontrack.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.jstring.Localizable;
import net.sf.jstring.NonLocalizable;

@Data
@AllArgsConstructor
public class Decoration {

    private final Localizable title;
    private final String cls;

    public Decoration(String title) {
        this(title, "");
    }

    public Decoration(String title, String cls) {
        this(new NonLocalizable(title), cls);
    }
}
