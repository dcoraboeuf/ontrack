'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('ontrack.services', [])
    .factory('AuthenticationService', ['$rootScope', function ($rootScope) {

        function anonymous() {
            return !logged()
        }

        function logged() {
            return $rootScope.user && $rootScope.user != null
        }

        function accountFullName() {
            if (logged()) {
                return $rootScope.user.fullName
            } else {
                return ''
            }
        }

        return {
            anonymous: anonymous,
            logged: logged,
            accountFullName: accountFullName
        }

    }])
;