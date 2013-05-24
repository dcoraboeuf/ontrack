//The build will inline common dependencies into this file.

//For any third party dependencies, like jQuery, place them in the lib folder.

//Configure loading modules from the lib directory,
//except for 'app' ones, which are in a sibling
//directory.
requirejs.config({
    baseUrl: 'static/js/lib',
    paths: {
        'app': '../app',
        'template': '../app/template',
        'bootstrap': '../../../static/bootstrap/js/bootstrap.min',
        'jquery-ui': '../../../static/jquery/ui/js/jquery-ui-1.9.2.custom.min'
    },
    shim: {
        'jquery-ui': {
            deps: ['jquery']
        },
        'bootstrap': {
            deps: ['jquery-ui']
        }
    }
});

require(['jquery','jquery-ui','bootstrap','app/controller/information-message','common','dynamic','app/controller/header']);