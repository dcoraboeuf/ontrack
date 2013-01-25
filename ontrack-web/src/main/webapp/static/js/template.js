var Template = function () {
	
	function Table () {
		this._lines = [];
	}
	
	Table.prototype.render = function () {
		var html = '<table class="table"><tbody>'
		  + this._lines.join('\n')
		  + '</tbody></table>';
		return html;
	};
	
	Table.prototype.lines = function (items, lineTemplate) {
		for (var i = 0 ; i < items.length ; i++) {
			this._lines.push(lineTemplate(items[i], i));
		}
		return this;
	};
	
	function Markup (tag) {
		this._tag = tag;
		this._body = [];
	}
	
	Markup.prototype.render = function (item) {
		var content = '';
		$.each (this._body, function (index, bodyItem) {
			if ($.isFunction(bodyItem)) {
				content += bodyItem(item);
			} else {
				content += bodyItem;
			}
		});
		return '<{0}>{1}</{0}>'.format(this._tag, content);
	};
	
	Markup.prototype.td = function (property) {
		return this.body(function (item) {
			return Template.td(property).render(item);
		});
	};
	
	Markup.prototype.body = function (content) {
		this._body.push(content);
		return this;
	};
	
	Markup.prototype.row = function (item) {
		var markup = this;
		return function (item) {
			return new Markup('tr').body(markup.render(item)).render(item);
		};
	};
	
	return {
		table: function () {
			return new Table();
		},
		td: function (property) {
			return new Markup('td').body(function (item) {
				return item[property].toString().html();
			});
		}
	};
	
} ();