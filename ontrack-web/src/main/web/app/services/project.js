angular.module('ontrack.service.project', ['ontrack.config'])
    .service('projectService', function ($http, config) {
        return {
            projectList: function (successFn, errorMsgFn) {
                $http
                    .get(config.api('project'))
                    .success(function (data) {
                        successFn(data)
                    })
            },
            createProject: function (name, description, success, error) {
                $http
                    .post(config.api('project'), {
                        name: name,
                        description: description
                    })
                    .success(success || angular.noop)
                    .error(error || angular.noop);
            }
        }
    })
;