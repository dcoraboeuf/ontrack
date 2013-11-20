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
    .service('errorService', function ($interpolate, $log, notificationService) {
        var self = {};
        self.errorMsg = function (text, status) {
            if (status == 401) {
                return '[ECH-401] Not authenticated';
            } else if (status == 403) {
                return '[ECH-403] Forbidden access';
            } else if (status == 404) {
                return '[ECH-404] Resource not found';
            } else {
                return text;
            }
        };
        self.process = function (response) {
            if (response.config.ontrackIgnoreError) return;
            var status = response.status;
            var method = response.config.method;
            var url = response.config.url;
            // Logging
            var log = $interpolate('[app] HTTP error {{status}} for {{method}} {{url}}')({
                status: status,
                method: method,
                url: url
            });
            $log.error(log);
            // Displays a notification
            notificationService.error(
                self.errorMsg(
                    response.data,
                    status
                )
            );
        };
        return self;
    })
;