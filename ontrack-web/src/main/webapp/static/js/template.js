var Template = function () {

	var Tag = function (name, attributes, content) {
	
		return {
			render: function () {
				var html = '<{0}'.format(name);
				for (var key in attributes) {
					var value = attributes[key];
					if (typeof value === "boolean") {
						if (value) {
							html += ' {0}="{0}"'.format(key);
						}
					} else {
						html += ' {0}="{1}"'.format(key.html(), value);
					}
				}
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
		this._class = "";
		return {
			withRow: function (row) {
				this._row = row;
				return this;
			},
			withClass: function (css) {
				this._class += " " + css;
				return this;
			},
			render: function (items) {
				return new Tag('table', {'class': this._class}, new Tag('body', {}, this._row.tags(items))).render();
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
				return new Tag('td', getAttributes(item, attributes), value.html());
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
	
	function getAttributes (item, attributes) {
		var map = {};
		for (var key in attributes) {
			var property = attributes[key];
			var value = getValue(item, property);
			map[key] = value;
		}
		return map;
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