'use strict';

// Declare app level module which depends on filters, and services
var ontrack = angular.module('ontrack', [
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
        .run(function (securityService) {
            // Hides the loading element
            angular.element(document.getElementById('ontrack-loading')).remove();
            // Loading the initial security context (if available)
            securityService.init();
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

// BOOTSTRAPING SECTION

angular.element(document).ready(function () {
    // Bootstrap element
    var bootstrapElement = document.getElementById('ontrack-loading');
    // Bootstrap module
    var bootstrapModule = angular.module('ontrack.bootstrap', ['ontrack.config']);
    // Loading the default language localization map
    bootstrapModule.factory('bootstrapper', function ($http, $q, $log, config) {
        return {
            bootstrap: function () {
                $log.info('Initializing the application...');
                var deferred = $q.defer();
                // Loading the translations
                $http.get(config.api('localization/en/' + config.version))
                    .success(function (map) {
                        $log.info('Default translation map loaded.');
                        angular.module('ontrack').value('defaultTranslationMap', map);
                        // Starting the application
                        $log.info('Starting the application...');
                        angular.bootstrap(document, ['ontrack']);
                        // OK
                        $log.info('Bootstraping done.');
                        deferred.resolve();
                    })
                    .error(function () {
                        angular.element(document.getElementById('ontrack-loading-message'))
                            .removeClass('alert-info')
                            .addClass('alert-danger')
                            .text('Could not initialize application, configuration could not be loaded.');
                        deferred.reject();
                    })
                ;
                // OK
                return deferred.promise;
            }
        }
    });
    // Running the application after the bootstrap is complete
    bootstrapModule.run(function (bootstrapper) {
        bootstrapper.bootstrap().then(function () {
            // Removing the container will destroy the bootstrap app
            angular.element(bootstrapElement).remove();
        });

    });
    // Actual bootstrapin
    angular.bootstrap(bootstrapElement, ['ontrack.bootstrap']);
});