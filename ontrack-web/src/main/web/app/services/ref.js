angular.module('ontrack.service.ref', ['ontrack.config'])
    .service('languages', function ($http, config) {
        return {
            list: function (successFn) {
                return $http.get(config.api('languages')).success(successFn);
            }
        }
    })
;