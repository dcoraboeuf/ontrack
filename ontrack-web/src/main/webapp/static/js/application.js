// Prototypes

String.prototype.format = function() {
	var args = arguments;
	return this.replace(/\{\{|\}\}|\{(\d+)\}/g, function(m, n) {
		if (m == "{{") {
			return "{";
		}
		if (m == "}}") {
			return "}";
		}
		return args[n];
	});
};

String.prototype.html = function() {
	return $('<i></i>').text(this).html();
};

String.prototype.htmlWithLines = function() {
	var text = this.html();
	return text.replace(/\n/g, '<br/>');
};

// Global functions

function loc () {
	var code = arguments[0];
	var text = l[code];
	if (text != null) {
		var params = []	;
		for (var i = 1 ; i < arguments.length ; i++) {
			params.push(arguments[i]);
		}
		return text.format (params);
	} else {
		return "##" + code + "##";
	}
}

// Application singleton

var Application = function () {
	
	function dialogAndSubmit (dialogId, dialogTitle, method, url, onSuccessFn) {
		dialog (
			dialogId,
			dialogTitle,
			function (closeFn) {
				submit (
					dialogId,
					method, url, 
					function (data) {
						// Does something with the data
						onSuccessFn(data);
						// Closes the dialog
						closeFn();
					},
					function (message) {
				  		$('#' + dialogId + '-error').html(message.htmlWithLines());
				  		$('#' + dialogId + '-error').show();
				});
			});
	}
	
	/**
	 * Opens a dialog defined by the <code>dialogId</code> HTML fragment.
	 * @param dialogId ID of a HTML section
	 * @param dialogTitle Title of the dialog
	 * @param onSubmitFn <code>(. => .) => .</code> - Function to call when the <code>submit</code> button
	 * is triggered. The <code>onSubmitFn</code> is called with a function
	 * (no argument) which is responsible to close the dialog. 
	 */
	function dialog (dialogId, dialogTitle, onSubmitFn) {
		// Sets the submit function
		$('#' + dialogId).unbind('submit');
		$('#' + dialogId).submit(function () {
			onSubmitFn(function () {
				$('#' + dialogId).dialog('close');
			});
			return false;
		});
		// Sets the cancel button (if any)
		$('#' + dialogId + '-cancel').unbind('click');
		$('#' + dialogId + '-cancel').click(function () {
			$('#' + dialogId).dialog('close');
		});
		// Shows the dialog
		$('#' + dialogId).dialog({
			title: dialogTitle,
			width: 450,
			modal: true
		});
	}
	
	function values (baseId) {
		var data = {};
		$('#' + baseId + ' input').each (function (index, field) {
			var name = field.getAttribute('name');
			var value = field.value;
			data[name] = value;
		});
		$('#' + baseId + ' textarea').each (function (index, field) {
			var name = field.getAttribute('name');
			var value = field.value;
			data[name] = value;
		});
		return data;
	}
	
	/**
	 * Submit a set of data defined in a form.
	 * @param baseId ID of the HTML form
	 * @param method HTTP method to use ('GET', 'POST'...)
	 * @param url URL to call
	 * @param successFn <code>(JSON => .)</code> Function called in case of success.
	 * @param errorMessageFn <code>(String => .)</code> Function called in case of error.
	 * @see #ajax
	 */
	function submit (baseId, method, url, successFn, errorMessageFn) {
		// Collects all fields values
		var data = values (baseId);
		// Call
		ajax (method, url, data, successFn, errorMessageFn);
	}
	
	function onAjaxError(jqXHR, textStatus, errorThrown, errorMessageFn) {
	  	if (errorMessageFn) {
	  		if (jqXHR.status == 500 && jqXHR.responseText && jqXHR.responseText != '') {
	  			errorMessageFn(jqXHR.responseText);
	  		} else {
	  			var message = getAjaxError(loc('client.error.general'), jqXHR, textStatus, errorThrown);
	  			errorMessageFn(message);
	  		}
	  	} else {
	  		Application.displayAjaxError (loc('client.error.general'), jqXHR, textStatus, errorThrown);
	  	}
	}
	
	/**
	 * @param method HTTP method to use ('GET', 'POST'...)
	 * @param url URL to call
	 * @param data Data to send as JSON
	 * @param successFn <code>(JSON => .)</code> Function called in case of success.
	 * @param errorMessageFn <code>(String => .)</code> Function called in case of error.
	 */
	function ajax (method, url, data, successFn, errorMessageFn) {
		$.ajax({
			type: method,
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(data),
			dataType: 'json',
			success: function (data) {
				successFn(data);
			},
			error: function (jqXHR, textStatus, errorThrown) {
				onAjaxError(jqXHR, textStatus, errorThrown, errorMessageFn);
			}
		});
	}
	
	function ajaxGet (url, successFn, errorMessageFn) {
		$.ajax({
			type: 'GET',
			url: url,
			dataType: 'json',
			success: function (data) {
				successFn(data);
			},
			error: function (jqXHR, textStatus, errorThrown) {
				onAjaxError(jqXHR, textStatus, errorThrown, errorMessageFn);
			}
		});
	}
	
	function ajaxDelete (url, successFn, errorMessageFn) {
		$.ajax({
			type: 'DELETE',
			url: url,
			dataType: 'json',
			success: function (data) {
				successFn(data);
			},
			error: function (jqXHR, textStatus, errorThrown) {
				onAjaxError(jqXHR, textStatus, errorThrown, errorMessageFn);
			}
		});
	}
	
	function confirmAndCall (text, callback) {
		$('<div>{0}</div>'.format(text)).dialog({
			title: loc('general.confirm.title'),
			dialogClass: 'confirm-dialog',
			modal: true,
			buttons: {
				Ok: function () {
					$( this ).dialog( "close" );
					callback();
				},
				Cancel: function () {
					$( this ).dialog( "close" );
				}
			}
		});
	}
	
	function confirmIDAndCall (id, callback) {
		var text = document.getElementById(id).value;
		confirmAndCall(text, callback);
	}
	
	function displayError (text) {
		$('<div>{0}</div>'.format(text.htmlWithLines())).dialog({
			title: loc('client.error.title'),
			modal: true,
            buttons: {
                Ok: function() {
                    $( this ).dialog( "close" );
                }
            }
		});
	}
	
	function getAjaxError (message, jqXHR, textStatus, errorThrown) {
		return '{0}\n[{1}] {2}'.format(message, jqXHR.status, jqXHR.statusText);
	}
	
	function displayAjaxError (message, jqXHR, textStatus, errorThrown) {
		var text = getAjaxError(message, jqXHR, textStatus, errorThrown);
		displayError(text);
	}
	
	function changeLanguage (lang) {
		if (location.search.indexOf("language") > -1) {
	    	location.search = location.search.replace(/language=[a-z][a-z]/, "language=" + lang);
		} else if (location.search == "") {
			var url = location.href;
			if (url.substr(url.length - 1) == '?') {
				location.href += "language=" + lang;
			} else {
				location.href += "?language=" + lang;
			}
		} else {
			location.href += "&language=" + lang;
		}
	}

	function validate (selector, test) {
		if (test) {
			$(selector).removeClass("invalid");
			return true;
		} else {
			$(selector).addClass("invalid");
			return false;
		}
	}
	
	function loading (selector, loading) {
		$(selector).empty();
		if (loading) {
			$(selector).append('<div class="loading">{0}</div>'.format(loc('general.loading')));
		}
	}
	
	var templates = {};
	
	function loadInit (id, templateFn) {
		// Registers the function
		templates["load-" + id] = templateFn;
		// Initial load
		load (id, templateFn);
	}
	
	function reload (id) {
		// Gets the load function
		var templateFn = templates["load-" + id];
		// Reloads
		load (id, templateFn);
	}
		
	function load (id, templateFn) {
		// Gets the loading information
		var url = $('#' + id).attr('data-url');
		if (url) {
			console.log('Loading "{0}" into "{1}"'.format(url, id));
			// Starts loading
			loading("#" + id + '-loading', true);
	  		$('#' + id + '-error').hide();
			// Call
			ajaxGet (
				url,
				function (data) {
					// Loading done
					loading("#" + id + '-loading', false);
					// Clears the container
					$('#' + id + '-list').empty();
					// Template
					var html = templateFn(data);
					$('#' + id + '-list').append(html);
				},
				function (message) {
			  		$('#' + id + '-error').html(message.htmlWithLines());
			  		$('#' + id + '-error').show();
					loading("#" + id + '-loading', false);
				});
		} else {
			throw 'No "data-url" is defined on element "{0}"'.format(id);
		}
	}

	function extractEntity (value) {
	    var pos = value.indexOf('/');
	    if (pos > 0) {
	        return value.substring(0, pos);
	    } else {
	        return value;
	    }
	}
	
	function deleteEntity (entity, id, backUrl) {
		var url = 'ui/manage/{0}/{1}'.format(entity, id);
		ajaxGet (
			url,
			function (o) {
				confirmAndCall(
					loc('{0}.delete.prompt'.format(extractEntity(entity)), o.name),
					function () {
						ajaxDelete (
							url,
							function () {
								location = backUrl;
							},
							displayError);
					});
			},
			displayError);
	}
	
	return {
		dialog: dialog,
		submit: submit,
		dialogAndSubmit: dialogAndSubmit,
		confirmAndCall: confirmAndCall,
		confirmIDAndCall: confirmIDAndCall,
		displayError: displayError,
		displayAjaxError: displayAjaxError,
		getAjaxError: getAjaxError,
		changeLanguage: changeLanguage,
		validateTextAsName: function (selector) {
			var value = $(selector).val();
			var trimmedValue = value.trim();
			return validate (selector, trimmedValue == value && trimmedValue != "");
		},
		validateTextAsTrimmed: function (selector) {
			var value = $(selector).val();
			var trimmedValue = value.trim();
			return validate (selector, trimmedValue == value);
		},
		validateConfirmation: function (source, confirmation) {
			var value = $(source).val();
			var confirmValue = $(confirmation).val();
			return validate (confirmation, confirmValue == value);
		},
		loading: loading,
		loadInit: loadInit,
		load: load,
		reload: reload,
		deleteEntity: deleteEntity,
		ajaxGet: ajaxGet
	};
	
} ();