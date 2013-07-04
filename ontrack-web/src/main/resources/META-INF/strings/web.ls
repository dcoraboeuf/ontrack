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

general.empty
    en -> No entry
    fr -> Aucune entrée

general.more
    en -> More...
    fr -> Plus...

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

net.ontrack.web.support.UploadTooBigException
    en -> File too big. Maximum is {0}K.
    fr -> Fichier trop gros. Le maximum est de {0} K.

[login]

login
    en -> Sign in
    fr -> Se connecter

login.user
    en -> User
    fr -> Utilisateur

login.password
    en -> Password
    fr -> Mot de passe

logout
    en -> Sign out
    fr -> Se déconnecter

[profile]

profile
    en -> Profile
    fr -> Profil

profile.changePassword
    en -> Change password
    fr -> Changer de mot de passe

profile.changePassword.ok
    en -> Password has been changed.
    fr -> Votre mot de passe a été changé.

profile.changePassword.nok
    en -> Password has not been changed. The old password may have been incorrect.
    fr -> Votre mot de passe n'a pas été changé. L'ancien mot de passe était peut-être incorrect.

[password]

password
    en -> Password change
    fr -> Changement de mot de passe

password.user
    en -> User
    fr -> Utilisateur

password.oldPassword
    en -> Old password
    fr -> Ancien mot de passe

password.newPassword
    en -> New password
    fr -> Nouveau mot de passe

password.newPassword.confirm
    en -> Confirmation
    fr -> Confirmation

password.submit
    en -> Change
    fr -> Changer

password.confirmationNok
    en -> The password confirmation is not correct.
    fr -> La confirmation du mot de passe est incorrecte.

[settings]

settings
    en -> Settings
    fr -> Configuration

settings.security
    en -> Security settings
    fr -> Configuration de la sécurité

settings.mail
    en -> Mail settings
    fr -> Configuration du courriel

settings.extension.saved
    en -> {0} saved.
    fr -> {0} sauvegardée.

settings.general
    en -> General configuration
    fr -> Configuration générale

[extensions]

extensions
    en -> Extensions
    fr -> Extensions

extensions.dependencies
    en -> Depends on
    fr -> Dépend de

[settings.general]

settings.general.baseUrl
    en -> Base URL
    fr -> URL de référence

settings.general.saved
    en -> General configuration has been saved.
    fr -> La configuration générale a été sauvegardée.

[security.ldap]

ldap.enabled
    en -> Enable LDAP authentication
    fr -> Activer l'authentification par LDAP

ldap.host
    en -> LDAP server
    fr -> Serveur LDAP

ldap.port
    en -> LDAP server port
    fr -> Port du serveur LDAP

ldap.search.base
    en -> LDAP search base
    fr -> Base de recherche LDAP

ldap.search.filter
    en -> LDAP filter
    fr -> Filtre LDAP

ldap.user
    en -> LDAP user
    fr -> Utilisateur LDAP

ldap.password
    en -> LDAP password
    fr -> Mot de passe LDAP

ldap.fullNameAttribute
    en -> Full name attribute
    fr -> Attribut pour le nom complet

ldap.emailAttribute
    en -> Email attribute
    fr -> Attribut pour le courriel

ldap.saved
    en -> LDAP configuration has been saved.
    fr -> La configuration LDAP a été sauvegardée.

[mail]

mail.saved
    en -> Mail configuration has been saved.
    fr -> La configuration du courriel a été sauvegardée.


mail.host
    en -> Mail server
    fr -> Serveur de courriel

mail.user
    en -> User
    fr -> Utilisateur

mail.password
    en -> Password
    fr -> Mot de passe

mail.authentication
    en -> Authentication
    fr -> Authentification

mail.startTls
    en -> Start TLS
    fr -> Démarrer TLS

mail.replyToAddress
    en -> Reply to address
    fr -> Adresse de retour

[accounts]

accounts
    en -> Accounts
    fr -> Utilisateurs

accounts.ldap-warning
    en -> The LDAP is not enabled and some users may not be able to connect.
    fr -> La configuration LDAP n'est pas activée et quelques utilisateurs pourraient ne pas pouvoir se connecter.

account.new
    en -> Create a new account
    fr -> Créer un nouvel utilisateur

account.name
    en -> Name
    fr -> Nom

account.fullName
    en -> Full name
    fr -> Nom complet

account.email
    en -> eMail
    fr -> Courriel

account.role
    en -> Role
    fr -> Rôle

account.role.ROLE_ADMIN
    en -> Administrator
    fr -> Administrateur
account.role.ROLE_CONTROLLER
    en -> Controller
    fr -> Contrôleur
account.role.ROLE_USER
    en -> User
    fr -> Utilisateur
account.role.ROLE_ADMIN.help
    en -> Administrators can: manage other accounts, manage all entities (projects, branches...).
    fr -> Les administrateurs peuvent gérer les autres comptes, gérer toutes les entités (projets, branches, ...).
account.role.ROLE_CONTROLLER.help
    en -> Controllers can create builds and validation runs. This role is typically given to automated tools like Jenkins.
    fr -> Les contrôleurs peuvent créer des builds et exécuter des validations. Ce rôle est typiquement attribué à des outils comme Jenkins.
account.role.ROLE_USER.help
    en -> Users can enter comments and add new statuses for builds and validation runs.
    fr -> Les utilisateurs peuvent ajouter des commentaires et des informations de statut aux builds et aux exécutions de validations.

account.mode
    en -> Authentication mode
    fr -> Mode d'authentification

account.mode.builtin
    en -> Built in
    fr -> Prédéfini

account.mode.ldap
    en,fr -> LDAP

account.password
    en -> Password
    fr -> Mot de passe

account.password.confirm
    en -> Confirm password
    fr -> Confirmation du mot de passe

account.password.confirm.incorrect
    en -> Password confirmation is incorrect
    fr -> La confirmation du mot de passe est incorrecte.

account.delete
    en -> Account deletion
    fr -> Suppression d'un compte utilisateur

account.delete.message
    en -> Do you really want to delete the following account?
    fr -> Voulez-vous vraiment supprimer le compte suivant ?

account.deleted
    en -> Account has been deleted.
    fr -> L'utilisateur a été supprimé.

account.update
    en -> Account update
    fr -> Mise à jour d'un compte utilisateur

account.updated
    en -> Account has been updated.
    fr -> L'utilisateur a été mis à jour.

[home]

home
	en -> Home page
	fr -> Page d'accueil

home.projects
	en -> Projects
	fr -> Projets

[project]

project.empty
    en -> No project has been created yet.
    fr -> Aucun projet n'a encore été créé.

project.create
	en -> New project
	fr -> Nouveau projet

project.update
	en -> Update project
	fr -> Modifier projet
	
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

branch.empty
    en -> No branch has been created yet.
    fr -> Aucune branche n'a encore été créée.

branch.create
	en -> New branch
	fr -> Nouvelle branche

branch.update
	en -> Update branch
	fr -> Modifier branche

branch.switch
    en -> Switch to
    fr -> Aller à

branch.clone
    en -> Clone
    fr -> Cloner

branch.clone.general
    en -> General
    fr -> Général

branch.clone.original
    en -> Original name
    fr -> Nom initial

branch.clone.properties
    en -> Branch properties
    fr -> Propriétés de la branche

branch.clone.properties.regex
    en -> Regular expression to be replaced by
    fr -> Expression régulière à remplacer par

branch.clone.validation_stamp.properties
    en -> Validation stamps properties
    fr -> Propriétés des validations

branch.clone.promotion_level.properties
    en -> Promotion level properties
    fr -> Propriétés des niveaux de promotions

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

branch.nobuild
    en -> No build has been done yet on this branch or the filter is too restrictive.
    fr -> Aucun build n'a encore été exécuté sur cette branche ou le filtre est trop restrictif.

branch.promotion_levels
	en -> Promotion levels
	fr -> Niveaux de promotion

branch.validation_stamps
	en -> Validation stamps
	fr -> Validations

branch.actions
    en -> Branch actions
    fr -> Actions sur la branche

[validation_stamps]

validation_stamp.empty
    en -> No validation stamp has been created yet.
    fr -> Aucune validation n'a encore été créée.

validation_stamp.create
	en -> New validation stamp
	fr -> Nouvelle validation

validation_stamp.update
	en -> Update validation stamp
	fr -> Modifier validation
	
validation_stamp.create.title
	en,fr -> @[validation_stamp.create]

validation_stamp.delete.prompt
	en -> Are you sure to delete the "{0}" validation stamp and all its associated information?
	fr -> Etes-vous sûr(e) de supprimer la validation "{0}" et toutes ses informations associées?

validation_stamp.comment.add
    en -> Add comment
    fr -> Ajouter commentaire

validation_stamp.comments
    en -> Comments
    fr -> Commentaires

validation_stamp.name
	en,fr -> @[model.name]
validation_stamp.description
	en,fr -> @[model.description]
validation_stamp.owner
    en -> Owner
    fr -> Responsable
validation_stamp.owner.none
    en -> None
    fr -> Aucun
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

validation_stamp.owner.change
    en -> Change the ownership
    fr -> Changer le responsable

validation_stamp.filter
    en -> Filter validation stamps
    fr -> Sélectionner les validations

validation_stamp.filter.all
    en -> Select all
    fr -> Tout sélectionner

validation_stamp.filter.none
    en -> Select none
    fr -> Tout désélectionner

validation_stamp.filter.enabled
    en -> Enabled
    fr -> Actif

[promotion_level]

promotion_level.manage
    en -> Manage promotion levels
    fr -> Gérer les niveaux de promotion

promotion_level.empty
    en -> No promotion level has been created yet.
    fr -> Aucun niveau de promotion n'a encore été créé.

promotion_level.create
	en -> New promotion level
	fr -> Nouveau niveau de promotion

promotion_level.update
	en -> Update promotion level
	fr -> Modifier niveau de promotion

promotion_level.name
	en,fr -> @[model.name]
promotion_level.description
	en,fr -> @[model.description]
promotion_level.autoPromote
    en -> Auto promotion
    fr -> Auto promotion
promotion_level.autoPromote.help
    en -> ...
        This promotion level is automatically granted when all the associated
        validation stamps have been passed.
        ...
    fr -> ...
        Ce niveau de promotion est assigné automatiquement quand toutes les
        validations ont été exécutées avec succès.
        ...
promotion_level.image
	en,fr -> Image
promotion_level.image.edit
	en -> Change image
	fr -> Changer l'image
promotion_level.image.placeholder
	en -> Select an image
	fr -> Sélectionnez une image
promotion_level.image.help
	en -> PNG image, max. 4K
	fr -> Image PNG, max. 4K
promotion_level.image.success
	en -> Successful upload of the image.
	fr -> L'image a été chargée correctement.

promotion_level.validation_stamps
    en -> Validation stamps
    fr -> Validations

promotion_level.validation_stamps.none
    en -> No validation stamp associated to this promotion level.
    fr -> Aucune validation n'est associée à ce niveau de promotion.

promotion_level.management.dropzone
    en -> Drop validation stamps here
    fr -> Ajouter les validations içi

promotion_level.management.dropzone.free
    en -> Drop free validation stamps here
    fr -> Ajouter les validations libres içi

promotion_level.management.dndhelp
    en -> Drag and drop validation stamps into the promotion levels to associate them.
    fr -> Déplacez les validations vers les niveaux de promotion pour les associer.

promotion_level.management.auto
    en -> Enable auto-promotion
    fr -> Activer l'auto-promotion

promotion_level.management.notauto
    en -> Disable auto-promotion
    fr -> Désactiver l'auto-promotion

promotion_level.promotions
    en -> Promotions
    fr -> Promotions

[builds]

build.create
    en -> Create build
    fr -> Créér un build

build.update
    en -> Update build
    fr -> Modifier build

build.validation_stamps
	en -> Validation stamps
	fr -> Validations

build.validation_stamps.none
    en -> None
    fr -> Aucune

build.validation_stamps.runAndPassed
    en -> Run & passed
    fr -> Exécutées avec succès

build.validation_stamps.runAndFailed
    en -> Run & failed
    fr -> Exécutées sans succès

build.validation_stamps.notruns
    en -> Not runs
    fr -> Non exécutées

build.validation_stamps.none
    en -> None
    fr -> Aucune

build.promotion_levels
    en -> Promotions
    fr -> Promotions

build.promotion_levels.none
    en -> No promotion
    fr -> Aucune promotion

build.delete
    en -> Delete
    fr -> Supprimer

build.delete.prompt
    en -> You are about to delete the build "{0}". This will delete any associated data. Are you sure to go on?
    fr -> Vous êtes sur le point de supprimer le build "{0}". Cela supprimera toutes les données associées. Voulez-vous continuer ?

build.promote
    en -> Promote
    fr -> Promouvoir

build.promote.date
    en -> Promotion date
    fr -> Date de promotion

build.promote.submit
    en,fr -> @[build.promote]

build.promote.title
    en -> Promotion of build {0}
    fr -> Promotion du build {0}

build.cleanup
    en -> Clean-up configuration
    fr -> Configuration de la purge

build.cleanup.retention
    en -> Retention days (0 to keep everything)
    fr -> Jours de rétention (0 pour tout garder)

build.cleanup.excludedPromotionLevels
    en -> Excluded promotion levels
    fr -> Niveaux de promotion exclus

[runs]

validationRun
    en -> Run
    fr -> Exécution

validation_run.delete.prompt
	en -> Are you sure to delete the "{0}" validation run and all its associated information?
	fr -> Etes-vous sûr(e) de supprimer l'exécution "{0}" et toutes ses informations associées?


validationRun.notRun
    en -> Not run
    fr -> Non exécuté

validationRun.history
    en -> History
    fr -> Historique


validationRun.history.thisRun
    en -> This run
    fr -> Cette exécution

validationRun.history.thisBuild
    en -> This build
    fr -> Ce build

validationRun.history.allBuilds
    en -> Other builds
    fr -> Autres builds

[validationRunStatus]

validationRunStatus.new
    en -> Update status
    fr -> Mettre à jour le statut

validationRunStatus.status
    en -> Status
    fr -> Statut

validationRunStatus.status.none
    en -> Comment only
    fr -> Commentaire

validationRunStatus.description
    en -> Comment
    fr -> Commentaire

[query]

query
    en -> Filter
    fr -> Filtrer

query.submit
    en -> Search
    fr -> Rechercher

query.clear
    en -> Clear
    fr -> Effacer

query.limit
    en -> Limit
    fr -> Limite

query.sincePromotionLevel
    en -> Since last promotion level
    fr -> Depuis le dernier niveau de promotion

query.withPromotionLevel
    en -> With promotion level
    fr -> Avec le niveau de promotion

query.withValidationStamp
    en -> With validation stamp
    fr -> Avec la validation

query.withValidationStamp.status
    en -> with status
    fr -> avec le statut

query.sinceValidationStamp
    en -> Since last validation stamp
    fr -> Depuis la dernière validation

query.sinceValidationStamp.status
    en -> with status
    fr -> avec le statut

query.withProperty
    en -> With property
    fr -> Avec la propriété

query.withPropertyValue
    en -> of value
    fr -> de valeur

[events]

event.list
	en -> Activity
	fr -> Activité

[properties]

properties.add
    en -> Add a property
    fr -> Ajouter une propriété

properties.add.choice
    en -> Choose a property to add
    fr -> Choisissez la propriété à ajouter

[subscription]

subscription.enable
    en -> Subscribe to the events
    fr -> S'abonner aux événements

subscription.disable
    en -> Unsubscribe from the events
    fr -> Se désabonner des événements

subscription.enabled
    en -> Subscription is enabled.
    fr -> L'abonnement est activé.

subscription.disabled
    en -> Subscription is disabled.
    fr -> L'abonnement est désactivé.

entityUnsubscription
    en -> Cancelling subscription
    fr -> Annuler un abonnement

entityUnsubscription.submit
    en -> OK
    fr -> OK

entityUnsubscription.done
    en -> The subscription has been cancelled.
    fr -> L'abonnement a été annulé.

subscription.management
    en -> Subscriptions
    fr -> Abonnements

subscription.management.none
    en -> No subscription.
    fr -> Aucun abonnement.

admin.subscriptions
    en -> Manage all subscriptions
    fr -> Gestion de tous les abonnements

admin.subscriptions.none
    en -> Not one subscription!
    fr -> Aucune subscription !

[search]

search
    en -> Search results
    fr -> Résultats de la recherche

search.noResult
    en -> No result was returned by the search.
    fr -> Aucun résultat n'a été trouvé.

search.submit
    en -> Search
    fr -> Chercher

[dashboard]

dashboard
    en -> Dashboard
    fr -> Tableau de bord

dashboard.general
    en -> General dashboard
    fr -> Tableau de bord général

dashboard.project
    en -> {0} project dashboard
    fr -> Tableau de bord pour le projet {0}

dashboard.branch
    en -> {0}/{1} branch dashboard
    fr -> Tableau de bord pour la branche {0}/{1}

dashboard.admin
    en -> Dashboard set-up
    fr -> Configuration du tableau de bord

dashboard.admin.help
    en -> Select the validation stamps that must appear in the dashboard
    fr -> Sélectionnez les validations qui doivent apparaître dans le tableau de bord
