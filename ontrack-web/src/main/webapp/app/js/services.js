'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('ontrack.services', [])
    .factory('AuthenticationService', ['$rootScope', '$http', 'config', 'ErrorService', function ($rootScope, $http, config, errorService) {

        $rootScope.logged = false;
        $rootScope.anonymous = true;
        $rootScope.accountFullName = '';

        function authenticationOk(authentication) {
            $rootScope.user = authentication;
            $rootScope.logged = true;
            $rootScope.anonymous = false;
            $rootScope.accountFullName = authentication.fullName;
        }

        function init() {
            $http
                .get(config.api('auth/authenticate'))
                .success(authenticationOk)
                .error(function () {
                    // Does nothing
                })
        }

        function authenticate(name, password, callbackFn, errorMessageFn) {
            $http
                .get(
                config.api('auth/authenticate'),
                {
                    headers: {
                        'Authorization': 'Basic ' + btoa(name + ':' + password)
                    }
                })
                .success(function (authentication) {
                    authenticationOk(authentication);
                    callbackFn(authentication);
                })
                .error(function (text, status) {
                    errorMessageFn(errorService.errorMsg(text, status))
                })
        }

        function logout(callbackFn) {
            return $http.get(config.api('auth/logout')).success(function () {
                $rootScope.logged = false;
                $rootScope.anonymous = true;
                $rootScope.accountFullName = '';
                callbackFn();
            })
        }

        return {
            init: init,
            authenticate: authenticate,
            logout: logout
        }

    }])
    .factory('ErrorService', function () {
        return {
            errorMsg: function (text, status) {
                if (status == 401) {
                    return 'Not authenticated';
                } else {
                    return text;
                }
            }
        }
    })
;