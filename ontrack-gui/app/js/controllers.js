'use strict';

/* Controllers */

angular.module('ontrack.controllers', [])
    .controller('NavBarCtrl', ['$rootScope', '$scope', '$location', 'AuthenticationService', function ($rootScope, $scope, $location, authenticationService) {
        $scope.isNavbarCollapsed = false;
        $scope.collapseNavbar = function () {
            $scope.isNavbarCollapsed = !$scope.isNavbarCollapsed;
        }
        $scope.signout = function () {
            authenticationService.logout(function () {
                $location.path('/home')
            })
        }
    }])
    .controller('SignInCtrl', ['$rootScope', '$scope', '$http', '$location', 'AuthenticationService', 'config', function ($rootScope, $scope, $http, $location, authenticationService, config) {
        $scope.name = '';
        $scope.password = '';
        $scope.signin = function () {
            $http
                .get(config.server + '/api/auth/authenticate',
                {
                    headers: {
                        'Authorization': 'Basic ' + btoa($scope.name + ':' + $scope.password)
                    }
                })
                .success(function (authentication) {
                    // Stores the authentication object
                    authenticationService.authenticate(authentication);
                    // TODO Redirect to the page in the scope
                    $location.path('/home');
                })
        }
    }])
    .controller('ProjectListCtrl', ['$scope', '$http', 'config', function ($scope, $http, config) {
        $http
            .get(config.server + '/api/project')
            .success(function (data) {
                $scope.projects = data
            })
    }])
    .controller('ProjectCtrl', ['$scope', '$routeParams', '$http', 'config', function ($scope, $routeParams, $http, config) {
        $http
            .get(config.server + '/api/project/' + $routeParams.projectName)
            .success(function (data) {
                $scope.project = data
            })
    }])
;