angular.module('ontrack.service.account', ['ontrack.config'])
    .service('accountService', function ($http, config) {
        var self = {};
        self.getAccountList = function () {
            return $http.get(config.api('admin/account'));
        };
        return self;
    })
;