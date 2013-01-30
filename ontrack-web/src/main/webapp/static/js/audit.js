var Audit = function () {
	
	function auditTemplate (items) {
		return Template.list(items, audit);
	}
	
	function audit (item) {
		return item.html + ' <span class="elapsed">- <span title="{1}">{0}</span></span>'.format(item.elapsed.html(), item.timestamp.html());
	}
	
	return {
		auditTemplate: auditTemplate
	};
	
} ();