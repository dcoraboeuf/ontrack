[core]

net.ontrack.backend.EventNotRelatedException
	en,fr -> [E-001] Event [{0}] is not related to any entity.

net.ontrack.backend.EntityIdNotFoundException
	en -> [E-002] {0} ID "{1}" cannot be found.

net.ontrack.backend.ConfigurationKeyMissingException
    en -> [E-003] Configuration key [{0}] is missing.

net.ontrack.backend.security.CannotInitializeLDAPException
    en -> [E-004] Cannot initialize the LDAP.

net.ontrack.backend.TemplateNotFoundException
    en -> [E-005] Cannot find template with name {0}.

net.ontrack.backend.TemplateMergeException
    en -> [E-006] Error while merging template {0}.

net.ontrack.backend.security.AsAdminCallException
    en -> [E-007] Error while running code as administrator.

[input]

net.ontrack.backend.EntityNameNotFoundException
	en -> {0} "{1}" cannot be found.
	fr -> {0} "{1}" n'a pas été trouvé.
net.ontrack.backend.ImageIncorrectMIMETypeException
	en -> Image type "{0}" is incorrect - expected "{1}" instead.
	fr -> Le type d'image "{0}" n'est pas correct - "{1}" est attendu.
net.ontrack.backend.ImageTooBigException
	en -> Image size ({0}) is too big - maximum size is {1}.
	fr -> La taille de l'image ({0}) est trop grande - le maximum est {1}.
net.ontrack.backend.ImageCannotReadException
    en -> Cannot read image content.
    fr -> Impossible de lire le contenu de l'image.
net.ontrack.backend.PropertyScopeException
    en -> Property {0}.{1} cannot be applied to {2}.
    fr -> La propriété {0}.{1} n'est pas applicable à {2}.
net.ontrack.backend.BranchNoBuildFoundException
    en -> No build found on branch.
    fr -> Aucun build pour la branche.
net.ontrack.backend.ProjectAlreadyExistException
    en -> Project with name "{0}" already exists.
    fr -> Le projet de nom "{0}" existe déjà.
net.ontrack.backend.BranchAlreadyExistException
    en -> Branch with name "{0}" already exists.
    fr -> La branche de nom "{0}" existe déjà.
net.ontrack.backend.ValidationStampAlreadyExistException
    en -> Validation stamp with name "{0}" already exists.
    fr -> La validation de nom "{0}" existe déjà.
