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
            },
            data: { pageTitle: 'Home' }
        })
    })

    .controller('HomeCtrl', function HomeController($scope, projectService) {

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
                    project.statuses = statuses;
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