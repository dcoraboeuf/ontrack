angular.module('ontrack.branch.page', [
        'ui.router',
        'ontrack.service.core',
        'ontrack.service.project'
    ])

    .config(function config($stateProvider) {
        $stateProvider.state('branch', {
            url: '/project/{projectName:[A-Za-z0-9_\\.\\-]+}/branch/{branchName:[A-Za-z0-9_\\.\\-]+}',
            views: {
                "main": {
                    controller: 'BranchCtrl',
                    templateUrl: 'app/branch/branch-page.tpl.html'
                }
            },
            // TODO Branch name?
            data: { pageTitle: 'Branch' }
        })
    })

    .controller('BranchCtrl', function BranchCtrl($scope, $state, $stateParams, $translate, pageService, projectService) {
        // TODO Loads the branch
        projectService.getProject($stateParams.projectName, function (projectResource) {
            // Page definition
            $scope.page = {
                title: projectResource.name
            };
            // Breadcrumbs
            pageService.setBreadcrumbs([
                {
                    text: $translate('home'),
                    link: '/home'
                },{
                    text: projectResource.name,
                    link: '/project/' + projectResource.name
                }, {
                    text: 'TODO Branch'
                }
            ]);
        });
    })

;