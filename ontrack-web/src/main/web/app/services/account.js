angular.module('ontrack.service.account', [
        'ontrack.config',
        'ontrack.service.core'
    ])
    .service('accountService', function ($q, $http, config, messageService, notificationService) {
        var self = {};
        self.getAccountList = function () {
            return $http.get(config.api('admin/account'));
        };
        self.createAccount = function (name, fullName, email, roleName, mode, password, successFn) {
            $http.post(config.api('admin/account'), {
                name: name,
                fullName: fullName,
                email: email,
                roleName: roleName,
                mode: mode,
                password: password,
                passwordConfirm: password
            })
                .success(function (accountResource) {
                    notificationService.success(messageService.translate(
                        'account.created',
                        accountResource.name
                    ));
                    successFn();
                })
        };
        return self;
    })
;