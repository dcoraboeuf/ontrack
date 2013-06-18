# Git extension

extension.git
    en -> Git

git.remote
    en -> Git repository to clone
    fr -> Repository Git à cloner

git.branch
    en -> Git branch
    fr -> Branche Git

git.tag
    en -> Git tag pattern
    fr -> Motif pour les tags Git

[changelog]

git.changelog
    en -> Change log
    fr -> Changements

[import-builds]

git.import-builds
    en -> Import builds
    fr -> Importer les builds

git.import-builds.override
    en -> Override the existing builds
    fr -> Remplacer les builds existants

git.import-builds.tagPattern
    en -> Tag regular expression to extract the build name
    fr -> Expression regulière pour extraire le nom du build depuis le nom du tag

[errors]

net.ontrack.extension.git.service.GitProjectRemoteNotConfiguredException
    en -> [GIT-001] Project remote repository is not configured.

net.ontrack.extension.git.client.impl.GitRepositoryManagerException
    en -> [GIT-002] Cannot get the repository manager for remote at {0}

net.ontrack.extension.git.client.impl.GitException
    en -> [GIT-003] Git API exception

net.ontrack.extension.git.client.impl.GitNotSyncException
    en -> [GIT-004] Git repository not initialized. A call to the sync() method is probably missing.

net.ontrack.extension.git.client.impl.GitIOException
    en -> [GIT-005] Git IO exception
