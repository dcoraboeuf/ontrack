angular.module('ontrack.project.create', [
        'ui.router',
        'ontrack.service.project'
    ])

    .config(function config($stateProvider) {
        $stateProvider.state('project-create', {
            url: '/project-create',
            views: {
                "main": {
                    controller: 'ProjectCreateCtrl',
                    templateUrl: 'app/project/project-create.tpl.html'
                }
            }
        })
    })

    .controller('ProjectCreateCtrl', function ProjectCreateCtrl($scope, $state, $translate, pageService, projectService) {

        // Breadcrumbs
        pageService.setBreadcrumbs([
            {
                text: $translate('home'),
                link: '/home'
            }, {
                text: $translate('project.create')
            }
        ]);

        $scope.name = '';
        $scope.description = '';
        $scope.error = undefined;
        $scope.create = function () {
            projectService.createProject(
                $scope.name,
                $scope.description,
                function (project) {
                    $state.go('home');
                    // TODO $state.go('project/' + project.name)
                }
            );
        }
    })

;