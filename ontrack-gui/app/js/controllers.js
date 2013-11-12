'use strict';

/* Controllers */

angular.module('ontrackApp.controllers', [])
    .controller('ProjectListCtrl', ['$scope', '$http', function ($scope, $http) {
        $scope.projects = [{
            name: 'EBANK',
            description: 'eBanking project',
            links: {
                self: '#/project/EBANK'
            }
        }, {
            name: 'ontrack',
            description: 'ontrack @ ontrack',
            links: {
                self: '#/project/ontrack'
            }
        }]
    }])
;