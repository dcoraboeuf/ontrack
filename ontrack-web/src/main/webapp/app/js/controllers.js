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
    .controller('SignInCtrl', ['$scope', '$location', 'AuthenticationService', function ($scope, $location, authenticationService) {
        $scope.name = '';
        $scope.password = '';
        $scope.signin = function () {
            authenticationService.authenticate($scope.name, $scope.password, function () {
                // TODO Redirect to the page in the scope
                $location.path('/home');
            })
        }
    }])
    .controller('ProjectListCtrl', ['$scope', '$http', 'config', function ($scope, $http, config) {
        $http
            .get(config.api('project'))
            .success(function (data) {
                $scope.projects = data;
                $scope.projectCreate = data.links.projectCreate;
            })
    }])
    .controller('ProjectCreateCtrl', ['$scope', '$http', '$location', 'config', 'ErrorService', function ($scope, $http, $location, config, errorService) {
        $scope.name = '';
        $scope.description = '';
        $scope.error = '';
        $scope.create = function () {
            $http
                .post(config.api('project'), {
                    name: $scope.name,
                    description: $scope.description
                })
                .success(function () {
                    $location.path('/project/' + $scope.name)
                })
                .error(function (text, status) {
                    $scope.error = errorService.errorMsg(text, status)
                })
        }
    }])
    .controller('ProjectCtrl', ['$scope', '$routeParams', '$http', 'config', function ($scope, $routeParams, $http, config) {
        $http
            .get(config.api('project/' + $routeParams.projectName))
            .success(function (data) {
                $scope.project = data
            })
    }])
;