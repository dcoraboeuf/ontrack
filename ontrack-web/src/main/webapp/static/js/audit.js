var Audit = function () {

    function subscribe (filter) {
        AJAX.get({
            loading: {
                el: '#subscription'
            },
            url: 'gui/event/subscribe?u=1' + filter,
            successFn: function (data) {
                $('#subscription').attr('src', 'static/images/unsubscription.png');
                $('#subscription').attr('title', loc('subscription.disable'));
                $('#subscription').unbind('click');
                $('#subscription').click(function () {
                    unsubscribe(filter);
                });
                // Notification
                $('#subscription-notification-message').text(loc('subscription.enabled'));
                $('#subscription-notification').show();
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
                $('#subscription').attr('src', 'static/images/subscription.png');
                $('#subscription').attr('title', loc('subscription.enable'));
                $('#subscription').unbind('click');
                $('#subscription').click(function () {
                    subscribe(filter);
                });
                // Notification
                $('#subscription-notification-message').text(loc('subscription.disabled'));
                $('#subscription-notification').show();
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