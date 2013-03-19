var Audit = function () {
	
	return {
		auditTemplate: function (filter) {
		    return Template.config({
		        url: 'gui/event?u=1' + filter,
		        more: true,
		        refresh: true,
		        render: Template.asTableTemplate('auditTemplate')
		    });
		}
	};
	
} ();