angular.module('ontrack.project.page', [
        'ui.router',
        'ontrack.service.project',
        'ontrack.service.dialog'
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

    .controller('ProjectCtrl', function ProjectCtrl($scope, $state, $stateParams, $translate, projectService, messageService, dialog) {
        // Loads the project
        projectService.getProject($stateParams.projectName, function (projectResource) {
            // Page definition
            $scope.page = {
                title: projectResource.name,
                breadcrumbs: [
                    {
                        text: $translate('home'),
                        link: '/home'
                    }
                ],
                description: projectResource.description,
                commands: [
                    {
                        id: 'project-delete',
                        name: 'general.delete',
                        icon: 'trash-o',
                        link: projectResource.links['deleteProject'],
                        action: function () {
                            dialog.confirm(messageService.translate('project.delete.prompt', projectResource.name)).then(function () {
                                projectService.deleteProject(projectResource.name).then(function () {
                                    $state.go('home')
                                })
                            });
                        }
                    }
                ],
                close: function () {
                    $state.go('home')
                }
            };
        });
        // Loads the project branch statuses
        projectService.loadProjectBranchStatus($stateParams.projectName, function (branchLastStatusResourceListResource) {
            $scope.branchLastStatusResourceListResource = branchLastStatusResourceListResource;
        });
    })

;