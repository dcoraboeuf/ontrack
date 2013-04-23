package net.ontrack.extension.pkg;

import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PackageExtension extends ExtensionAdapter {

    public static final String EXTENSION = "package";

    @Autowired
    public PackageExtension(PackagePropertyDescriptor packagePropertyDescriptor) {
        super(
                EXTENSION,
                Collections.singletonList(packagePropertyDescriptor));
    }

}
