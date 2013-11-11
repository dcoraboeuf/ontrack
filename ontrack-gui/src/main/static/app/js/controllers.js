var ontrackApp = angular.module('ontrackApp', []);

ontrackApp.controller('ProjectListCtrl', function ProjectListCtrl($scope) {
    $scope.projects = [
        {'name': 'EBANK', 'description': 'eBanking project'},
        {'name': 'ontrack', 'description': 'ontrack @ ontrack'}
    ];
});