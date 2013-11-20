angular.module('ontrack.service.core', [])
    .service('notificationService', function () {
        var self = {
            message: undefined,
            messageType: 'default'
        };
        self.clear = function () {
            self.message = undefined;
            self.messageType = 'success';
        };
        self.success = function (message) {
            self.message = message;
            self.messageType = 'success';
        };
        self.error = function (message) {
            self.message = message;
            self.messageType = 'danger';
        };
        return self;
    })
    .service('errorService', function () {
        return {
            errorMsg: function (text, status) {
                if (status == 401) {
                    return '[ECH-401] Not authenticated';
                } else if (status == 403) {
                    return '[ECH-403] Forbidden access';
                } else if (status == 404) {
                    return '[ECH-404] Resource not found';
                } else {
                    return text;
                }
            }
        }
    })
;