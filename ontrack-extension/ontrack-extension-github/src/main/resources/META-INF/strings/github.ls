extension.github
    en -> GitHub

github.project
    en -> GitHub project
    fr -> Projet GitHub

github.project.description
    en -> Name of the GitHub project, like <user>/<repository>.
    fr -> Nom du projet GitHub, comme <utilisateur>/<repository>.

github.authentication
    en,fr -> GitHub API token

github.authentication.description
    en -> ...
        API token used when connecting to GitHub in order
        to allow for more requests per hour. If this token
        is not set, you may encounter problems if you connect
        too often to the GitHub repository.
        ...
    fr -> ...
        Token utilisé lors de la connexion à l'API GitHub
        afin d'allouer plus de requêtes par heure. Si vous
        ne renseignez pas ce champs, vous risquez de rencontrer
        des problèmes si vous vous connectez trop souvent au
        repository GitHub.
        ...

[changelog]

github.changelog.issues
    en -> GitHub issues
    fr -> Tickets GitHub

github.changelog.issues.astext
    en -> Export as text
    fr -> Exporter comme texte

github.changelog.issues.id
    en -> #

github.changelog.issues.title
    en -> Title
    fr -> Titre

github.changelog.issues.assignee
    en -> Assignee
    fr -> Affectation

github.changelog.issues.milestone
    en -> Milestone

github.changelog.issues.createdAt
    en -> Created
    fr -> Créée

github.changelog.issues.updatedAt
    en -> Updated
    fr -> Mise à jour

github.changelog.issues.closedAt
    en -> Closed
    fr -> Fermée

[search]

github.search.issue
    en -> Issue {0} for project {1}
    fr -> Ticket {0} pour le projet {1}

[issue]

github.issue.state.closed
    en -> Closed
    fr -> Fermée

github.issue.state.open
    en -> Open
    fr -> Ouverte

github.issue.commit
    en -> Last commit
    fr -> Dernier commit

github.issue.createdAt
    en -> Created on {0} at {1}
    fr -> Créée le {0} à {1}

github.issue.updatedAt
    en -> Updated on {0} at {1}
    fr -> Modifiée le {0} à {1}

github.issue.commit.none
    en -> No commit is associated with this issue.
    fr -> Aucun commit n'est associé à ce ticket.

github.issue.commits
    en -> List of associated commits
    fr -> Liste des commits associés

github.commit.id
    en -> Id
    fr -> Id

github.commit.author
    en -> Author
    fr -> Auteur

github.commit.author.none
    en -> No assignee
    fr -> Aucune personne assignée

github.commit.message
    en -> Message
    fr -> Message

github.commit.commitTime
    en -> Commit time
    fr -> Date de commit

[errors]

net.ontrack.extension.github.client.OntrackGitHubClientException
    en -> [GITHUB-001] Error while accessing GitHub: {0}

net.ontrack.extension.github.service.GitHubIssueNotFoundException
    en -> [GITHUB-002] Cannot find issue {1} for project {0}.
    fr -> [GITHUB-002] Impossible de trouver le ticket {0} pour le projet {0}.
