# Localization for SVNExplorer

extension.svnexplorer
    en -> SVNExplorer

[sensibleFiles]

svnexplorer.sensibleFiles
    en -> List of sensible files
    fr -> Liste des fichiers sensibles

svnexplorer.sensibleFiles.description
    en -> ...
        Each line defines a regular expression that is used against
        the relative repository file paths in a change log in order
        to detect the files whose changes should be monitored. This
        list of files will be displayed in the "Information" tab
        of the change log.
        ...
    fr -> ...
        Chaque ligne définit une expression régulière qui sera utilisée
        pour les chemins relatifs au repository des fichiers dans un
        rapport de changement, afin de détecter les fichiers dont
        tout changement devrait être observé avec plus d'attention. La
        liste de ces fichiers sera affichée dans l'onglet "Information"
        du rapport de changement.
        ...

[changelog]

svnexplorer.changelog
    en -> Change log
    fr -> Changements

svnexplorer.changelog.summary
    en -> Summary
    fr -> Général

[revisions]

svnexplorer.changelog.revisions
    en -> Revisions
    fr -> Révisions

svnexplorer.changelog.revisions.revision
    en -> Revision
    fr -> Révision

svnexplorer.changelog.revisions.author
    en -> Author
    fr -> Auteur

svnexplorer.changelog.revisions.date
    en -> Date
    fr -> Date

svnexplorer.changelog.revisions.message
    en -> Message
    fr -> Message

svnexplorer.changelog.revisions.path
    en -> Path
    fr -> Chemin

[issues]

svnexplorer.changelog.issues.revision
    en -> Last revision
    fr -> Dernière révision

svnexplorer.changelog.issues.openAll
    en -> Open all issues in JIRA
    fr -> Ouvrir tous les tickets dans JIRA

[files]

svnexplorer.changelog.issues
    en -> Issues
    fr -> Tickets

svnexplorer.changelog.files
    en -> Files
    fr -> Fichiers

svnexplorer.changelog.files.path
    en -> Path
    fr -> Chemin

svnexplorer.changelog.files.changes
    en -> Changes
    fr -> Changements

[changelog.info]

svnexplorer.changelog.info
    en -> Information
    fr -> Information

svnexplorer.changelog.info.title
    en -> Displays high level information about the status of this change log, but can take time in order to be displayed.
    fr -> Affiche des informations de haut niveau à propos des changements, mais peut prendre du temps à être affiché.

svnexplorer.changelog.info.status
    en -> Issue statuses
    fr -> Statuts des tickets

svnexplorer.changelog.info.status.status
    en -> Status
    fr -> Status

svnexplorer.changelog.info.status.count
    en -> Count
    fr -> Nombre

svnexplorer.changelog.info.files
    en -> Sensible files
    fr -> Fichiers sensibles

svnexplorer.changelog.info.files.none
    en -> No sensible file was changed.
    fr -> Aucun fichier sensible n'a été changé.

[search]

svnexplorer.search.revision
    en -> Revision {0} in repository {1}
    fr -> Révision {1} dans le repository {1}

svnexplorer.search.key
    en -> Issue {0} is associated with some code in repository {1).
    fr -> Le ticket {0} est associé à du code dans le repository {1}.

[revision]

svnexplorer.revision
    en -> Revision {0}
    fr -> Révision {0}

svnexplorer.revision.builds
    en -> Associated builds
    fr -> Builds associés

svnexplorer.revision.builds.none
    en -> The revision does not appear in any build.
    fr -> Cette révision n'apparait dans aucun build.

svnexplorer.revision.promotions
    en -> Associated promotions
    fr -> Promotions associées

svnexplorer.revision.promotions.none
    en -> The revision was never promoted.
    fr -> Cette révision n'a jamais été promue.

svnexplorer.revision.promotions.notForThisPromotionLevel
    en -> The revision was never promoted to this promotion level.
    fr -> Cette révision n'a jamais été promue à ce niveau de promotion.

[issue]

svnexplorer.issue
    en -> Issue {0}
    fr -> Ticket {0}

svnexplorer.issue.revisions
    en -> Associated revisions
    fr -> Révisions associées

svnexplorer.issue.main
    en -> Source
    fr -> Source

svnexplorer.issue.merged
    en -> Merged at {0}
    fr -> Réintégré à {0}

[branch-history]

svnexplorer.branch-history
    en -> Branch history
    fr -> Historique des branches

svnexplorer.branch-history.title
    en -> Branch history for {0}
    fr -> Historique des branches pour {0}

svnexplorer.branch-history.path
    en -> Path
    fr -> Chemin

svnexplorer.branch-history.branch
    en -> Branch
    fr -> Branche

svnexplorer.branch-history.latestBuild
    en,fr -> @[branch.lastBuild]

svnexplorer.branch-history.latestBuild.none
    en -> No build
    fr -> Aucun build

svnexplorer.branch-history.promotions
    en -> Promotions
    fr -> Promotions

[rootPath]

svnexplorer.rootPath
    en -> SVN root path
    fr -> Chemin racine SVN

svnexplorer.rootPath.description
    en -> ...
        Path relative to the SVN repository that define the root when
        looking for the branches history. It is typically the trunk
        of all the branches.
        ...
    fr -> ...
        Chemin relatif au repository SVN qui définit le point de départ
        quand la recherche de l'historique des branches est effectuée. Il
        s'agit typiquement du 'trunk' associé à toutes les branches.
        ...

[errors]

net.ontrack.extension.svnexplorer.service.SVNExplorerNoBuildPathDefinedForBranchException
    en -> No build path property has been defined for the branch. Contact the administrator.
    fr -> Aucun chemin de build n'a été défini pour la branche. Contactez l'administrateur.

net.ontrack.extension.svnexplorer.ui.ChangeLogUUIDException
    en -> Change log with UUID {0} has not found. Maybe it has expired.
    fr -> Les changements d'UUID {0} n'ont pas été trouvés. Ils ont peut-être expiré.

net.ontrack.extension.svnexplorer.service.NoCommonAncestorException
    en -> No common ancestor was found between the builds histories.
    fr -> Aucun ancêtre common n'a été trouvé entre les historiques des builds.

net.ontrack.extension.svnexplorer.service.ProjectHasRootPathException
    en -> Project {0} has no SVN root path defined.
    fr -> Le project {0} n'a aucun chemin racine de défini.
