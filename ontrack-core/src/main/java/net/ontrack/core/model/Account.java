package net.ontrack.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectACL;
import net.ontrack.core.security.ProjectFunction;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Account {

    private final int id;
    private final String name;
    private final String fullName;
    private final String email;
    private final String roleName;
    private final String mode;
    private Locale locale;
    private Set<GlobalFunction> globalACL;
    private Set<ProjectACL> projectACL;

    public Account(int id, String name, String fullName, String email, String roleName, String mode, Locale locale) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.email = email;
        this.roleName = roleName;
        this.mode = mode;
        this.locale = locale;
    }

    public boolean isGranted(GlobalFunction fn) {
        return globalACL != null && globalACL.contains(fn);
    }

    public boolean isGranted(ProjectFunction fn, int id) {
        return projectACL != null && projectACL.contains(new ProjectACL(fn, id));
    }

    public Account withGlobalACL(GlobalFunction fn) {
        if (globalACL == null) {
            globalACL = new HashSet<>();
        }
        globalACL.add(fn);
        return this;
    }

    public Account withProjectACL(ProjectFunction fn, int id) {
        if (projectACL == null) {
            projectACL = new HashSet<>();
        }
        projectACL.add(new ProjectACL(fn, id));
        return this;
    }

}
