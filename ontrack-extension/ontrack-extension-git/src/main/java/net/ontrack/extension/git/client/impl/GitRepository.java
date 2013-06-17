package net.ontrack.extension.git.client.impl;

import java.io.File;

public interface GitRepository {

    File wd();

    String getRemote();

    String getId();

    GitRepository sync();

}
