angular.module('ontrack.project.page', [
        'ui.router',
        'ontrack.service.project'
    ])

    .config(function config($stateProvider) {
        $stateProvider.state('project', {
            url: '/project/{projectName:[A-Za-z0-9_\\.\\-]+}',
            views: {
                "main": {
                    controller: 'ProjectCtrl',
                    templateUrl: 'app/project/project.tpl.html'
                }
            },
            // TODO Project name?
            data: { pageTitle: 'Project' }
        })
    })

    .controller('ProjectCtrl', function ProjectCtrl($scope, $state, $stateParams, projectService) {
        $scope.projectName = $stateParams.projectName;
        // Page definition
        $scope.page = {
            title: $scope.projectName
        };
    })

;