angular.module('ontrack.service.core', [])
    .service('errorService', function () {
        return {
            errorMsg: function (text, status) {
                if (status == 401) {
                    return 'Not authenticated';
                } else if (status == 403) {
                    return 'Forbidden access';
                } else {
                    return text;
                }
            }
        }
    })
;