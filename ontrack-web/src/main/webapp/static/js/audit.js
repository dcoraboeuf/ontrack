var Audit = function () {
	
	function auditTemplate (items) {
		return Template.table().withClass("table").withClass("table-hover").withRow(
				Template.row()
					.cell(function (item) {
						return audit(item);
					})
			).render(items);
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