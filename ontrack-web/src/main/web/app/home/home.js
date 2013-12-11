angular.module('ontrack.home', [
        'ui.router',
        'ontrack.service.project'
    ])

    .config(function config($stateProvider) {
        $stateProvider.state('home', {
            url: '/home',
            views: {
                "main": {
                    controller: 'HomeCtrl',
                    templateUrl: 'app/home/home.tpl.html'
                }
            }
        })
    })

    .controller('HomeCtrl', function HomeController($scope, $translate, projectService, pageService) {

        // Breadcrumbs
        pageService.setBreadcrumbs([
            {
                text: $translate('home')
            }
        ]);

        // Loads the project list
        function loadProjectList() {
            projectService.projectList(function (projectList) {
                $scope.projectList = projectList;
                $scope.projectCreate = projectList.links.projectCreate;
                // Loads branch statuses for all projects
                loadProjectBranchStatus();
            });
        }

        // Loads branch statuses for all projects
        function loadProjectBranchStatus() {
            angular.forEach($scope.projectList.items, function (project) {
                projectService.loadProjectBranchStatus(project.name, function (statuses) {
                    // Pre-processing
                    angular.forEach(statuses.items, function (status) {
                        var promotion = null;
                        for(var i = status.promotions.length - 1; i >= 0; i--) {
                            var currentPromotion = status.promotions[i];
                            if (currentPromotion.build) {
                                promotion = currentPromotion;
                                break;
                            }
                        }
                        status.lastPromotion = promotion;
                        status.notPromoted = !promotion;
                    });
                    // OK
                    project.statuses = statuses.items;
                })
            });
        }

        // On logged out event, reloads the project list
        $scope.$on("$ontrackLoggedOut", function () {
            loadProjectList();
        });
        // Loads the project list
        loadProjectList();
    })

;