package net.ontrack.extension.pkg;

import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PackageExtension extends ExtensionAdapter {

    public static final String EXTENSION = "package";

    private final PackagePropertyDescriptor packagePropertyDescriptor;

    @Autowired
    public PackageExtension(PackagePropertyDescriptor packagePropertyDescriptor) {
        super(EXTENSION);
        this.packagePropertyDescriptor = packagePropertyDescriptor;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Collections.singletonList(packagePropertyDescriptor);
    }
}
