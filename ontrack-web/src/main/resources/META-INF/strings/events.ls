[events]

event.ago
	en -> {0} ago by {1}
	fr -> il y a {0} par {1}

event.PROJECT_GROUP_CREATED
	en -> Group $PROJECT_GROUP$ was created.
	fr -> Le groupe $PROJECT_GROUP$ a été créé.

event.PROJECT_CREATED
	en -> Project $PROJECT$ has been created.
	fr -> Le project $PROJECT$ a été créé.

event.PROJECT_DELETED
	en -> Project $project$ has been deleted.
	fr -> Le project $project$ a été supprimé.
	
event.BRANCH_CREATED
	en -> Branch $BRANCH$ has been created for the $PROJECT$ project.
	fr -> La branche $BRANCH$ a été créée pour le projet $PROJECT$.

event.BRANCH_DELETED
    en -> The $branch$ of the $project$ project has been deleted.
    fr -> La branche $branch$ du projet $project$ a été supprimée.
	
event.VALIDATION_STAMP_CREATED
	en -> The $VALIDATION_STAMP$ validation stamp has been created for the $BRANCH$ branch of the $PROJECT$ project.
	fr -> La validation $VALIDATION_STAMP$ a été créée pour la branche $BRANCH$ du projet $PROJECT$.

event.PROMOTION_LEVEL_CREATED
	en -> The $PROMOTION_LEVEL$ promotion level has been created for the $BRANCH$ branch of the $PROJECT$ project.
	fr -> Le niveau de promotion $PROMOTION_LEVEL$ a été créé pour la branche $BRANCH$ du projet $PROJECT$.

event.BUILD_CREATED
	en -> Build $BUILD$ has been created for the $BRANCH$ in the $PROJECT$ project.
	fr -> Le build $BUILD$ a été créé pour la branche $BRANCH$ du projet $PROJECT$.

event.VALIDATION_RUN_CREATED
	en -> $VALIDATION_RUN|Run$ of $VALIDATION_STAMP$ for build $BUILD$ of branch $BRANCH$ of $PROJECT$ is $status$.
	fr -> L'$VALIDATION_RUN|exécution$ de $VALIDATION_STAMP$ pour le build $BUILD$ de la branche $BRANCH$ de $PROJECT$ est $status$.

event.VALIDATION_RUN_STATUS
    en -> $VALIDATION_RUN|Run$ of $VALIDATION_STAMP$ for build $BUILD$ of branch $BRANCH$ of $PROJECT$ has changed its status to $status$.
	fr -> L'$VALIDATION_RUN|exécution$ de $VALIDATION_STAMP$ pour le build $BUILD$ de la branche $BRANCH$ de $PROJECT$ est passée au statut $status$.

event.VALIDATION_RUN_COMMENT
    en -> $author$ has added a comment on run #$VALIDATION_RUN$ of $VALIDATION_STAMP$ of build $BUILD$ of branch $BRANCH$ of project $PROJECT$: $comment$
    fr -> $author$ a ajouté un commentaire pour l'éxecution #$VALIDATION_RUN$ de $VALIDATION_STAMP$ du build $BUILD$ de la branche $BRANCH$ du projet $PROJECT$ : $comment$

event.PROMOTED_RUN_CREATED
    en -> Build $BUILD$ of branch $BRANCH$ of project $PROJECT$ has been promoted to $PROMOTION_LEVEL$.
    fr -> Le build $BUILD$ de la branche $BRANCH$ de $PROJECT$ a été promu vers $PROMOTION_LEVEL$.

event.VALIDATION_STAMP_DELETED
    en -> The $validationStamp$ validation stamp of $branch$ of the $project$ project has been deleted.
    fr -> La validation $validationStamp$ de la branche $branch$ du projet $project$ a été supprimée.
