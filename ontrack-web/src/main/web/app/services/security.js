angular.module('ontrack.service.security', ['ontrack.config', 'ontrack.service.core'])
    .service('securityService', function securityService($http, $log, config, errorService) {

        var self = {
            user: undefined
        };

        self.authenticationOk = function (user) {
            $log.debug('[sec] User logged in: ' + user.name);
            self.user = user;
        };

        self.init = function () {
            $http
                .get(config.api('auth/authenticate'), {
                    ontrackIgnoreError: true
                })
                .success(self.authenticationOk)
                .error(function () {
                    // Does nothing
                })
        };

        self.authenticate = function (name, password, callbackFn, errorMessageFn) {
            $http
                .get(
                config.api('auth/authenticate'),
                {
                    ontrackIgnoreError: true,
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

        self.logout = function (callbackFn) {
            return $http.get(config.api('auth/logout')).success(function () {
                $log.debug('[sec] User logged out: ' + self.user.name);
                self.user = undefined;
                callbackFn();
            })
        };

        return self;
    })
;