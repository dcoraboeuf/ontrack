angular.module('ontrack.service.security', ['ontrack.config', 'ontrack.service.core'])
    .service('securityService', function securityService($http, config, errorService) {
        return {
            // Properties
            user: undefined,
            // Methods
            authenticate: function authenticate(name, password, callbackFn, errorMessageFn) {
                $http
                    .get(
                    config.api('auth/authenticate'),
                    {
                        headers: {
                            'Authorization': 'Basic ' + btoa(name + ':' + password)
                        }
                    })
                    .success(function (user) {
                        this.authenticationOk(user);
                        callbackFn(user);
                    })
                    .error(function (text, status) {
                        errorMessageFn(
                            status == 403
                                ? 'Username and/or password incorrect'
                                : errorService.errorMsg(text, status)
                        )
                    })
            },
            authenticationOk: function authenticationOk(authentication) {
                this.user = authentication;
            }
        }
    })
;