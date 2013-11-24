'use strict';

// Declare app level module which depends on filters, and services
angular.module('ontrack', [
        'ui.bootstrap',
        'ui.router',
        'pascalprecht.translate',
        'ontrack-templates',
        'ontrack.config',
        'ontrack.directives',
        'ontrack.service.security',
        'ontrack.home',
        'ontrack.signin',
        'ontrack.project.create'
    ])
    .config(function ($httpProvider, $urlRouterProvider) {
        // Default error management
        $httpProvider.interceptors.push('httpErrorInterceptor');
        // Authentication using cookies and CORS protection
        $httpProvider.defaults.withCredentials = true;
        // Route set-up
        $urlRouterProvider.otherwise('/home');
    })
    .config(function ($translateProvider) {
        $translateProvider.preferredLanguage('en');
        $translateProvider.useLoader('$translateUrlLoader', {});
    })
    .factory('$translateUrlLoader', function ($q, $http, config) {
        return function (options) {
            var deferred = $q.defer();
            $http({
                url: config.api('localization/' + options.key + '/' + config.version),
                method: 'GET'
            })
                .success(function (data) {
                    deferred.resolve(data);
                })
                .error(function (data) {
                    deferred.reject(options.key);
                });
            return deferred.promise;
        }
    })
    .run(function (securityService, $translate) {
        securityService.init();
        $translate.refresh('en');
    })
    .factory('httpErrorInterceptor', function ($q, $log, $interpolate, notificationService, errorService) {
        return {
            'responseError': function (rejection) {
                errorService.process(rejection);
                // Standard behaviour
                return $q.reject(rejection);
            }
        }
    })
    .controller('AppCtrl', function AppCtrl($scope, $location, config, securityService, notificationService) {
        $scope.isNavbarCollapsed = false;
        $scope.version = config.version;
        // On state change
        $scope.$on('$stateChangeSuccess', function (event, toState) {
            // Clears any notification
            notificationService.clear();
            // Page title
            if (angular.isDefined(toState.data.pageTitle)) {
                $scope.pageTitle = toState.data.pageTitle + ' | ontrack';
            }
        });
        // Notifications
        $scope.hasNotification = function () {
            return angular.isDefined(notificationService.message);
        };
        $scope.notification = function () {
            return notificationService.message;
        };
        $scope.notificationType = function () {
            return notificationService.messageType;
        };
        $scope.closeNotification = function () {
            notificationService.clear();
        };
        // Looks for the user
        $scope.logged = function () {
            return angular.isDefined(securityService.user);
        };
        $scope.user = function () {
            return securityService.user;
        };
        // Collapses the navigation bar
        $scope.collapseNavbar = function () {
            $scope.isNavbarCollapsed = !$scope.isNavbarCollapsed;
        };
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
