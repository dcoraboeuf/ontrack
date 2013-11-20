'use strict';

// Declare app level module which depends on filters, and services
angular.module('ontrack', [
        'ui.bootstrap',
        'ui.router',
        'ontrack-templates',
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
    .controller('AppCtrl', function AppCtrl($scope, $location, config, securityService, notificationService) {
        $scope.isNavbarCollapsed = false;
        $scope.version = config.version;
        // Page title
        $scope.$on('$stateChangeSuccess', function (event, toState) {
            if (angular.isDefined(toState.data.pageTitle)) {
                $scope.pageTitle = toState.data.pageTitle + ' | ontrack';
            }
        });
        // Notifications
        $scope.hasNotification = function () {
            return angular.isDefined(notificationService.message);
        }
        $scope.notification = function () {
            return notificationService.message;
        }
        $scope.notificationType = function () {
            return notificationService.messageType;
        }
        $scope.closeNotification = function () {
            notificationService.clear();
        }
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
                // Success
                notificationService.success('You have been logged out.');
                // Broadcasts the logout event
                $scope.$broadcast('$ontrackLoggedOut');
                // Goes to the home page
                $location.path('/home');
            })
        }
    })
;
