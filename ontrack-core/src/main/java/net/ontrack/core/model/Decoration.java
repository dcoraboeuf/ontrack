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
    private final String iconPath;
    private final String link;

    public Decoration(Localizable title) {
        this(title, "", "", null);
    }

    public Decoration(String title, String cls) {
        this(new NonLocalizable(title), cls, "", null);
    }

    public Decoration withIconPath(String iconPath) {
        return new Decoration(title, cls, iconPath, link);
    }

    public Decoration withLink(String link) {
        return new Decoration(title, cls, iconPath, link);
    }

}
