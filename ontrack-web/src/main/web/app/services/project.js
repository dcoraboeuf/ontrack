angular.module('ontrack.service.project', ['ontrack.config'])
    .service('projectService', function ($http, config) {
        return {
            projectList: function (successFn, errorMsgFn) {
                $http
                    .get(config.api('project'))
                    .success(function (data) {
                        successFn(data)
                    })
                // TODO Error mgt
            }
        }
    })
;