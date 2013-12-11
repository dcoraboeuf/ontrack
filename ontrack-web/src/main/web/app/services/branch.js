angular.module('ontrack.service.branch', ['ontrack.config'])
    .service('branchService', function ($http, config) {
        return {
            getBranch: function (project, branch, success) {
                $http
                    .get(config.api('project/' + project + '/branch/' + branch))
                    .success(success);
            }
        }
    })
;