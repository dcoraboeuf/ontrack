var Template = function () {
	
	function Table () {
		this._lines = [];
	}
	
	Table.prototype.render = function () {
		var html = '<table class="table"><tbody>'
		  + this._lines
		  + '</tbody></table>';
		return html;
	};
	
	Table.prototype.lines = function (items, lineTemplate) {
		for (var i = 0 ; i < items.length ; i++) {
			this._lines.push(lineTemplate(items[i], i));
		}
		return this;
	};
	
	return {
		table: function () {
			return new Table();
		}
	};
	
} ();