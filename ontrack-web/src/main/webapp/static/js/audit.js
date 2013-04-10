var Audit = function () {

    function subscribe (filter) {
        AJAX.get({
            loading: {
                el: '#subscription'
            },
            url: 'gui/event/subscribe?u=1' + filter,
            successFn: function (data) {
                // TODO Changes the subscription icon
            }
        });
    }

    function unsubscribe (filter) {
        AJAX.get({
            loading: {
                el: '#subscription'
            },
            url: 'gui/event/unsubscribe?u=1' + filter,
            successFn: function (data) {
                // TODO Changes the subscription icon
            }
        });
    }

    function auditTemplate (filter) {
		    return Template.config({
		        url: 'gui/event?u=1' + filter,
		        more: true,
		        refresh: true,
		        render: Template.asTableTemplate('auditTemplate')
		    });
		}
	
	return {
	    subscribe: subscribe,
	    unsubscribe: unsubscribe,
		auditTemplate: auditTemplate
	};
	
} ();