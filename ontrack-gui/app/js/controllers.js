'use strict';

/* Controllers */

angular.module('ontrack.controllers', [])
    .controller('NavBarCtrl', ['$scope', function ($scope) {
        $scope.isNavbarCollapsed = false;
        $scope.collapseNavbar = function () {
            $scope.isNavbarCollapsed = !$scope.isNavbarCollapsed;
            console.log('Collapsed', $scope.isNavbarCollapsed);
        }
    }])
    .controller('SignInCtrl', [function () {

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