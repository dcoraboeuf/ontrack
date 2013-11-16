'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('ontrack.services', [])
    .factory('AuthenticationService', ['$rootScope', '$http', 'config', function ($rootScope, $http, config) {

        $rootScope.logged = false;
        $rootScope.anonymous = true;
        $rootScope.accountFullName = '';

        function authenticate(name, password, callbackFn) {
            $http
                .get(config.server + '/api/auth/authenticate',
                {
                    headers: {
                        'Authorization': 'Basic ' + btoa(name + ':' + password)
                    }
                })
                .success(function (authentication) {
                    $rootScope.user = authentication;
                    $rootScope.logged = true;
                    $rootScope.anonymous = false;
                    $rootScope.accountFullName = authentication.fullName;
                    callbackFn(authentication);
                })
        }

        function logout(callbackFn) {
            return $http.get(config.server + '/api/auth/logout').success(function () {
                $rootScope.logged = false;
                $rootScope.anonymous = true;
                $rootScope.accountFullName = '';
                callbackFn();
            })
        }

        return {
            authenticate: authenticate,
            logout: logout
        }

    }])
;