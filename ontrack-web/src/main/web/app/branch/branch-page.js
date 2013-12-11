angular.module('ontrack.branch.page', [
        'ui.router',
        'ontrack.service.core',
        'ontrack.service.branch'
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

    .controller('BranchCtrl', function BranchCtrl($scope, $state, $stateParams, $translate, pageService, branchService) {
        branchService.getBranch($stateParams.projectName, $stateParams.branchName, function (branchResource) {
            // Page definition
            $scope.page = {
                title: branchResource.name,
                close: function () {
                    $state.go('project', {
                        projectName: $stateParams.projectName
                    })
                }
            };
            // Breadcrumbs
            pageService.setBreadcrumbs([
                {
                    text: $translate('home'),
                    link: '/home'
                },{
                    text: branchResource.project,
                    link: '/project/' + branchResource.project
                }, {
                    text: branchResource.name
                }
            ]);
        });
    })

;