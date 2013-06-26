package net.ontrack.web.support.fm;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.web.gui.model.GUIAction;
import net.sf.jstring.Strings;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public abstract class AbstractFnExtensionActions implements TemplateMethodModel {

    protected final SecurityUtils securityUtils;
    private final Strings strings;
    private final ExtensionManager extensionManager;

    protected AbstractFnExtensionActions(Strings strings, ExtensionManager extensionManager, SecurityUtils securityUtils) {
        this.strings = strings;
        this.extensionManager = extensionManager;
        this.securityUtils = securityUtils;
    }

    @Override
    public Collection<GUIAction> exec(List list) throws TemplateModelException {
        // Gets the list of top level actions
        Collection<? extends ActionExtension> actions = getActions(extensionManager, list);
        // Filter on access rights
        actions = Collections2.filter(
                actions,
                new Predicate<ActionExtension>() {
                    @Override
                    public boolean apply(ActionExtension action) {
                        String role = action.getRole();
                        return role == null || securityUtils.hasRole(role);
                    }
                }
        );
        // OK
        return toGUIActions(actions);

    }

    private Collection<GUIAction> toGUIActions(Collection<? extends ActionExtension> actions) {
        // Gets the locale from the context
        final Locale locale = LocaleContextHolder.getLocale();
        // Converts to GUI actions
        return Collections2.transform(
                actions,
                new Function<ActionExtension, GUIAction>() {
                    @Override
                    public GUIAction apply(ActionExtension action) {
                        return new GUIAction(
                                action.getExtension(),
                                action.getName(),
                                action.getPath(),
                                strings.get(locale, action.getTitleKey())
                        );
                    }
                }
        );
    }

    protected abstract Collection<? extends ActionExtension> getActions(ExtensionManager extensionManager, List<String> arguments);
}
