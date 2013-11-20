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
        return self;
    })
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