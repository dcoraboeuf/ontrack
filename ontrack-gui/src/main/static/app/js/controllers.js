var ontrackApp = angular.module('ontrackApp', ['config']);

ontrackApp.controller('ProjectListCtrl', ['$scope', 'config', '$http', function ($scope, config, $http) {
    $http
        .get(config.server + '/ui/manage/project')
        .success(function (data) {
            $scope.projects = data;
        })
}]);