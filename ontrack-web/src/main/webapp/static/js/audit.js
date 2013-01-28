var Audit = function () {
	
	function auditTemplate (items) {
		return Template.list(items, audit);
	}
	
	function audit (item) {
		var key = "event." + item.audited + "." + (item.creation ? "created" : "updated");
		var text = loc(key, item.auditedName);
		return text;
	}
	
	return {
		auditTemplate: auditTemplate
	};
	
} ();