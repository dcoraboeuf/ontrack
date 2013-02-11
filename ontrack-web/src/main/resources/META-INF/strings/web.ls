[languages]

language.en
	en,fr -> English
language.fr
	en,fr -> Français
	
[client]

client.error.title
	en -> Error
	fr -> Erreur

client.error.general
	en -> Communication error
	fr -> Erreur de communication
	
[general]

general.cancel
	en -> Cancel
	fr -> Annuler

general.submit
	en -> Submit
	fr -> Envoyer	

general.close
	en -> Close
	fr -> Fermer
	
general.create
	en -> Create
	fr -> Créer
	
general.modify
	en -> Modify
	fr -> Modifier

general.delete
	en -> Delete
	fr -> Supprimer

general.confirm.title
	en,fr -> Confirmation
	
general.loading
	en -> Loading...
	fr -> Chargement en cours...
	
general.error.technical
	en -> Technical error
	fr -> Erreur technique
	
general.error.contact
	en -> Please report the following message and identifier to the ontrack administrator.
	fr -> Veuillez signaler le message et l'identifiant qui suivent à l'administrateur d'ontrack.

general.error.full
	en -> ...
		An error has occurred.\n
		@[general.error.contact]\n
		\n
		{0}\n
		Identifier: {1}
		...
	fr -> ...
		Une erreur est survenue.\n
		@[general.error.contact]\n
		{0}\n
		Identifiant : {1}
		...
	
# Error page
[error]

error
	en -> Error
	fr -> Erreur
	
error.message
	en -> ...
		An error has occurred. We are sorry for any inconvenience.
		...
	fr -> ...
		Une erreur s'est produite. Nous nous excusons pour le problème rencontré.
		...

error.back
	en -> Go back to the portal
	fr -> Revenir à l'acceuil

[login]

login.link
    en -> Sign in
    fr -> Se connecter

[home]

home
	en -> Home page
	fr -> Page d'accueil

home.projectgroups
	en -> Groups
	fr -> Groupes

home.projects
	en -> Projects
	fr -> Projets

[projectgroup]

projectgroup.create
	en -> New group
	fr -> Nouveau groupe

projectgroup.create.title
	en,fr -> @[projectgroup.create]

projectgroup.name
	en,fr -> @[model.name]
projectgroup.description
	en,fr -> @[model.description]

[project]

project.create
	en -> New project
	fr -> Nouveau projet
	
project.delete.prompt
	en -> Are you sure to delete the project "{0}" and all its associated information?
	fr -> Etes-vous sûr(e) de supprimer le projet "{0}" et toutes ses informations associées?

project.create.title
	en,fr -> @[project.create]

project.name
	en,fr -> @[model.name]
project.description
	en,fr -> @[model.description]
project.branches
	en,fr -> Branches

[branches]

branch.create
	en -> New branch
	fr -> Nouvelle branche
	
branch.delete.prompt
	en -> Are you sure to delete the "{0}" branch and all its associated information?
	fr -> Etes-vous sûr(e) de supprimer la branche "{0}" et toutes ses informations associées?

branch.create.title
	en,fr -> @[branch.create]

branch.name
	en,fr -> @[model.name]
branch.description
	en,fr -> @[model.description]
	
branches.builds
	en,fr -> Builds

branch.promotion_levels
	en -> Promotion levels
	fr -> Niveaux de promotion

branch.validation_stamps
	en -> Validation stamps
	fr -> Validations

[validation_stamps]

validation_stamp.create
	en -> New validation stamp
	fr -> Nouvelle validation
	
validation_stamp.create.title
	en,fr -> @[validation_stamp.create]
	
validation_stamp.name
	en,fr -> @[model.name]
validation_stamp.description
	en,fr -> @[model.description]
validation_stamp.image
	en,fr -> Image
validation_stamp.image.edit
	en -> Change image
	fr -> Changer l'image
validation_stamp.image.placeholder
	en -> Select an image
	fr -> Sélectionnez une image
validation_stamp.image.help
	en -> PNG image, max. 4K
	fr -> Image PNG, max. 4K
validation_stamp.image.success
	en -> Successful upload of the image.
	fr -> L'image a été chargée correctement.
	
[builds]

build.validation_stamps
	en -> Validation stamps
	fr -> Validations

[runs]

validationRun
    en -> Run
    fr -> Exécution
	
[events]

event.list
	en -> Activity
	fr -> Activité
