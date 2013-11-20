'use strict';

// Declare app level module which depends on filters, and services
angular.module('ontrack', [
        'ui.bootstrap',
        'ui.router',
        'ontrack.home',
        'ontrack.config',
        'ontrack.signin',
        'ontrack.service.security'
    ])
    .config(function ($httpProvider, $urlRouterProvider) {
        // Authentication using cookies and CORS protection
        $httpProvider.defaults.withCredentials = true;
        // Route set-up
        $urlRouterProvider.otherwise('/home');
    })
    // .run(['AuthenticationService', function (authenticationService) {
    //     authenticationService.init()
    // }])
    .controller('AppCtrl', function AppCtrl($scope, securityService) {
        $scope.isNavbarCollapsed = false;
        // Looks for the user
        $scope.logged = function () {
            return angular.isDefined(securityService.user);
        }
        $scope.user = function () {
            return securityService.user;
        }
        // Collapses the navigation bar
        $scope.collapseNavbar = function () {
            $scope.isNavbarCollapsed = !$scope.isNavbarCollapsed;
        }
    })
;
