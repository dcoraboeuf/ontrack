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
		var key = "audit." + item.audited + "." + (item.creation ? "created" : "updated");
		return key + "." + item.auditedId;
	}
	
	return {
		auditTemplate: auditTemplate
	};
	
} ();