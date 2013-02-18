var Audit = function () {
	
	function audit (item) {
		var html = '<div class="{0}">'.format(item.status!="" ? 'status-' + item.status : '');
		if (item.icon != "") {
			html += '<img width="24" src="{0}" class="event-icon" /> '.format(item.icon.html());
		}
		html += item.html;
		html += ' <span class="elapsed">- <span title="{1}">{0}</span></span>'.format(item.elapsed.html(), item.timestamp.html());
		html += '</div>';
		return html;
	}
	
	return {
		auditTemplate: function (filter) {
		    return Template.config({
		        url: 'gui/event?u=1' + filter,
		        more: true,
		        refresh: true,
		        render: Template.asTable(audit)
		    });
		}
	};
	
} ();