'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('ontrack.services', [])
    .factory('AuthenticationService', ['$rootScope', function ($rootScope) {
        return {
            anonymous: function () {
                return $rootScope.user
            }
        }
    }])
;