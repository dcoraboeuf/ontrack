angular.module('ontrack.service.security', ['ontrack.config', 'ontrack.service.core'])
    .service('securityService', function securityService($http, config, errorService) {

        var self = {
            user: undefined
        };

        self.authenticationOk = function (user) {
            self.user = user;
        };

        self.authenticate = function (name, password, callbackFn, errorMessageFn) {
            $http
                .get(
                config.api('auth/authenticate'),
                {
                    headers: {
                        'Authorization': 'Basic ' + btoa(name + ':' + password)
                    }
                })
                .success(function (user) {
                    self.authenticationOk(user);
                    callbackFn(user);
                })
                .error(function (text, status) {
                    errorMessageFn(
                        status == 403
                            ? 'Username and/or password incorrect'
                            : errorService.errorMsg(text, status)
                    )
                })
        };

        return self;
    })
;