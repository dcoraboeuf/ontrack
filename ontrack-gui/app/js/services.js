'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('ontrack.services', [])
    .factory('AuthenticationService', ['$rootScope', '$http', 'config', function ($rootScope, $http, config) {

        var user;

        function authenticate(authentication) {
            user = authentication;
        }

        function logout() {
            return $http.get(config.server + '/api/auth/logout');
        }

        function anonymous() {
            return !logged()
        }

        function logged() {
            return user && user != null
        }

        function accountFullName() {
            if (logged()) {
                return user.fullName
            } else {
                return ''
            }
        }

        return {
            authenticate: authenticate,
            logout: logout,
            anonymous: anonymous,
            logged: logged,
            accountFullName: accountFullName
        }

    }])
;