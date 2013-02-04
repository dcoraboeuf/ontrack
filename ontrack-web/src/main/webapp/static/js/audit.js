var Audit = function () {
	
	function auditTemplate (items) {
		return Template.list(items, audit);
	}
	
	function audit (item) {
		var html = '';
		if (item.icon != "") {
			html += '<img width="24" src="{0}" class="event-icon" /> '.format(item.icon.html());
		}
		html += item.html;
		html += ' <span class="elapsed">- <span title="{1}">{0}</span></span>'.format(item.elapsed.html(), item.timestamp.html());
		return html;
	}
	
	return {
		auditTemplate: auditTemplate
	};
	
} ();