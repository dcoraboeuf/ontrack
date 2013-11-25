angular.module('ontrack.service.project', ['ontrack.config'])
    .service('projectService', function ($http, config) {
        return {
            projectList: function (successFn) {
                $http
                    .get(config.api('project'))
                    .success(function (data) {
                        successFn(data)
                    })
            },
            loadProjectBranchStatus: function (projectName, successFn) {
                $http
                    .get(config.api('project/' + projectName + '/branch/status'))
                    .success(successFn)
            },
            createProject: function (name, description, success, error) {
                $http
                    .post(config.api('project'), {
                        name: name,
                        description: description
                    })
                    .success(success || angular.noop)
                    .error(error || angular.noop);
            },
            getProject: function (name, success) {
                $http
                    .get(config.api('project/' + name))
                    .success(success);
            }
        }
    })
;