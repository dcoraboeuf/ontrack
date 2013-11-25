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
        // Loads the project
        projectService.getProject($stateParams.projectName, function (projectResource) {
            // Page definition
            $scope.page = {
                title: projectResource.name,
                description: projectResource.description,
                close: function () {
                    $state.go('home')
                }
            };
        });
    })

;