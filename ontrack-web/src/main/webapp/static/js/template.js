var Template = function () {

	var Tag = function (name, attributes, content) {
	
		return {
			render: function () {
				var html = '<{0}'.format(name);
				// TODO Attributes
				if (content) {
					html += '>';
					html += renderContent(content);
					html += '</{0}>'.format(name);
				} else {
					html += '/>';
				}
				return html;
			}
		};
	};

	var Table = function () {
		this._row = null;
		return {
			withRow: function (row) {
				this._row = row;
				return this;
			},
			render: function (items) {
				return new Tag('table', {}, new Tag('body', {}, this._row.tags(items))).render();
			}
		};
	};

	var TableRow = function () {
		var cells = [];
		return {
			cell: function (property, attributes) {
				cells.push(new TableCell(this, property, attributes));
				return this;
			},
			tags: function (items) {
				var list = [];
				$.each (items, function (index, item) {
					var tds = [];
					$.each (cells, function (icell, cell) {
						tds.push(cell.tag(item));
					});
					list.push(new Tag('tr', {}, tds));
				});
				return list;
			}
		};
	};

	var TableCell = function (row, property, attributes) {
		return {
			tag: function (item) {
				var value = String(getValue(item, property));
				return new Tag('td', {}, value.html());
			}
		};
	};
	
	function getValue (o, property) {
		if ($.isFunction(property)) {
			return property(o);
		} else if (typeof property === "string" && property.length > 0 && property.charAt(0) == "$") {
			return o[property.substring(1)];
		} else {
			return property;
		}
	}
	
	function renderContent (content) {
		if ($.isFunction(content)) {
			return content();
		} else if ($.isArray(content)) {
			var html = '';
			$.each(content, function (i, c) {
				html += renderContent(c);
			});
			return html;
		} else if (content.render) {
			return content.render();
		} else {
			return content;
		}
	}
	
	return {
		table: function () {
			return new Table();
		},
		row: function () {
			return new TableRow();
		}
	};
	
} ();