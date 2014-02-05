# Git extension

extension.git
    en -> Git

git.remote
    en -> Git repository
    fr -> Repository Git

git.remote.description
    en -> URL to the Git repository to clone
    fr -> URL vers le repository Git à cloner

git.branch
    en -> Git branch
    fr -> Branche Git

git.branch.description
    en -> ...
        Name of the Git branch that is linked to this branch.
        It defaults to master.
        ...
    fr -> ...
        Nom de la branche Git à lier à cette branche. La valeur
        par défaut est "master".
        ...

git.tag
    en -> Git tag pattern
    fr -> Motif pour les tags Git

git.tag.description
    en -> ...
        Text that contains a * that will be
        replaced by the build name in order to get the name of the
        associated Git tag.
        ...
    fr -> ...
        Texte qui contient un * qui sera
        remplacé par le nom du build afin d'obtenir
        le tag Git correspondant.
        ...

git.commitLink
    en -> Commit link
    fr -> Lien pour commit

git.commitLink.description
    en -> ...
        URL to a page that can display details about a commit. This is
        a pattern where * is replaced by the actual commit hash.
        ...
    fr -> ...
        URL vers une page qui affiche les détails d'un commit donné. Il
        s'agit d'un texte où * est remplacé par le commit.
        ...

git.fileAtCommitLink
    en -> File at commit link
    fr -> Lien pour fichier et commit


git.fileAtCommitLink.description
    en -> ...
        URL to a page that can display details about a file for a given
        commit. This is a pattern where * is replaced by the actual
        commit hash and $ by the file relative path.
        ...
    fr -> ...
        URL vers une page qui affiche les détails d'un fichier pour
        un commit donné. Il s'agit d'un texte où * est remplacé par
        le commit et $ par le chemin relatif du fichier.
        ...

[changelog]

git.changelog
    en -> Change log
    fr -> Changements

git.changelog.summary
    en -> Summary
    fr -> Général

git.changelog.commits
    en -> Commits
    fr -> Commits

git.changelog.commits.id
    en,fr -> SHA-1

git.changelog.commits.author
    en -> Author
    fr -> Auteur

git.changelog.commits.committer
    en,fr -> Committer

git.changelog.commits.message
    en,fr -> Message

git.changelog.commits.timestamp
    en -> Timestamp
    fr -> Date

git.changelog.files
    en -> Files
    fr -> Fichiers

git.changelog.files.changeType
    en -> Change type
    fr -> Changement

git.changelog.files.changeType.ADD
    en -> Added
    fr -> Ajouté
git.changelog.files.changeType.COPY
    en -> Copied
    fr -> Copié
git.changelog.files.changeType.DELETE
    en -> Deleted
    fr -> Supprimé
git.changelog.files.changeType.MODIFY
    en -> Modified
    fr -> Modifié
git.changelog.files.changeType.RENAME
    en -> Renamed
    fr -> Renommé

git.changelog.files.path
    en -> Path
    fr -> Chemin

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

[search]

git.search.commit
    en -> Commit {0}
    fr -> Commit {0}

[commit]

git.commit
    en,fr -> Commit {0}

git.commit.builds
    en,fr -> Builds

git.commit.commitTime.detailed
    en -> {0} at {1}
    fr -> {0} à {1}

git.commit.builds.none
    en -> The commit does not appear in any build.
    fr -> Ce commit n'apparait dans aucun build.

git.commit.promotions
    en -> Associated promotions
    fr -> Promotions associées

git.commit.promotions.none
    en -> The commit was never promoted.
    fr -> Ce commit n'a jamais été promu.

git.commit.promotions.notForThisPromotionLevel
    en -> The commmit was never promoted to this promotion level.
    fr -> Ce commit n'a jamais été promu à ce niveau de promotion.

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

net.ontrack.extension.git.client.impl.GitCommitNotFoundException
    en -> [GIT-006] Cannot find any commit for object "{0}"

net.ontrack.extension.git.ui.ChangeLogUUIDException
    en -> [GIT-007] Change log with UUID {0} cannot be found

net.ontrack.extension.git.model.GitTagNameNoMatchException
    en -> [GIT-008] Tag "{0}" does not match the "{1}" pattern.

net.ontrack.extension.git.client.impl.GitCannotCloneException
    en -> [GIT-009] Cloning of repository seems to have failed at {0}.
