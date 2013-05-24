define(function () {

    function check (config, name) {
        if (!config || !config[name]) {
            throw '"{0}" parameter is not defined'.format(name);
        }
    }

    return {
        check: check
    }

});