package net.ontrack.backend.extension;

import net.ontrack.core.model.Decoration;
import net.ontrack.core.model.Entity;
import net.ontrack.core.support.InputException;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.decorator.DecorationService;
import net.ontrack.extension.api.decorator.EntityDecorator;
import net.sf.jstring.Localizable;
import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Service
public class DefaultDecorationsService implements DecorationService {

    private final Logger logger = LoggerFactory.getLogger(DecorationService.class);
    private final ExtensionManager extensionManager;
    private final Strings strings;

    @Autowired
    public DefaultDecorationsService(ExtensionManager extensionManager, Strings strings) {
        this.extensionManager = extensionManager;
        this.strings = strings;
    }

    @Override
    public List<Decoration> getDecorations(Entity entity, int entityId) {
        Collection<? extends EntityDecorator> decorators = extensionManager.getDecorators();
        List<Decoration> decorations = new ArrayList<>();
        // Gets all decorators
        for (EntityDecorator decorator : decorators) {
            if (decorator.getScope().contains(entity)) {
                Decoration decoration;
                try {
                    decoration = decorator.getDecoration(entity, entityId);
                } catch (Exception ex) {
                    // In case of error:
                    // 1. assigns an error decoration
                    decoration = new Decoration(new LocalizableMessage("DecorationService.error", decorator.getClass().getSimpleName())).withIconPath("static/images/decoration-error.png");
                    // 2. gets the error message
                    String message;
                    boolean stack;
                    if (ex instanceof Localizable) {
                        message = ((Localizable) ex).getLocalizedMessage(strings, Locale.ENGLISH);
                        stack = !(ex instanceof InputException);
                    } else {
                        message = ex.getMessage();
                        stack = true;
                    }
                    // 3. logs the error
                    if (stack) {
                        logger.error(String.format("[decoration] %s", message), ex);
                    } else {
                        logger.error(String.format("[decoration] %s", message));
                    }
                }
                if (decoration != null) {
                    decorations.add(decoration);
                }
            }
        }
        // TODO Sorting
        // OK
        return decorations;
    }
}
