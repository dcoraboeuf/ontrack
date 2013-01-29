var Audit = function () {
	
	function auditTemplate (items) {
		return Template.list(items, audit);
	}
	
	function audit (item) {
		return item.html;
	}
	
	return {
		auditTemplate: auditTemplate
	};
	
} ();