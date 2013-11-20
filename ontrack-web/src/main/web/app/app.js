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
    .run(function (securityService) {
        securityService.init()
    })
    .controller('AppCtrl', function AppCtrl($scope, $location, securityService) {
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
        // Signing out
        $scope.signout = function () {
            securityService.logout(function () {
                // TODO alertService.success('You have been logged out.');
                $location.path('/home');
                // Broadcasts the logout event
                $scope.$broadcast('$ontrackLoggedOut');
            })
        }
    })
;
