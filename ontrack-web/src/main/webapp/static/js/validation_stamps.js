var ValidationStamps = function () {
	
	function createValidationStamp (project, branch) {
		Application.dialogAndSubmit({
			id: 'validation_stamp-create-dialog',
			title: loc('validation_stamp.create.title'),
			url: 'ui/manage/project/{0}/branch/{1}/validation_stamp'.format(project,branch),
			successFn: function (data) {
				location = 'gui/project/{0}/branch/{1}/validation_stamp/{2}'.format(project,branch,data.name);
			}
		});
	}

	function updateValidationStamp (project, branch, validationStamp) {
	    var url = 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}'.format(project, branch, validationStamp);
	    AJAX.get({
            url: url,
            successFn: function (summary) {
                Application.dialogAndSubmit({
                    id: 'validation_stamp-update-dialog',
                    title: loc('validation_stamp.update'),
                    url: url,
                    method: 'PUT',
                    openFn: function () {
                        $('#validation_stamp-update-dialog-name').val(summary.name);
                        $('#validation_stamp-update-dialog-description').val(summary.description);
                    },
                    successFn: function (summary) {
                            location = 'gui/project/{0}/branch/{1}/validation_stamp/{2}'.format(summary.branch.project.name, summary.branch.name, summary.name);
                        }
                    });
            }
	    });
	}

    function up (project, branch, name) {
        AJAX.put({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/up'.format(project, branch, name),
            loading: {
                el: $('#validation-stamp-{0}-order-loading'.format(name)),
                mode: 'container'
            },
            successFn: function () {
                Template.reload('validation_stamps');
            }
        })
    }

    function down (project, branch, name) {
        AJAX.put({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/down'.format(project, branch, name),
            loading: {
                el: $('#validation-stamp-{0}-order-loading'.format(name)),
                mode: 'container'
            },
            successFn: function () {
                Template.reload('validation_stamps');
            }
        })
    }

    function changeOwner (project, branch, name) {
        // Gets the details of the validation stamp
        AJAX.get({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}'.format(
                project,
                branch,
                name
            ),
            successFn: function (summary) {
                // Gets the current owner
                var currentOwner = summary.owner ? summary.owner.id : 0;
                // Gets the list of potential owners
                AJAX.get({
                    url: 'ui/admin/accounts',
                    successFn: function (accounts) {
                        Application.dialog({
                            id: 'validation_stamp-owner-dialog',
                            title: loc('validation_stamp.owner.change'),
                            openFn: function () {
                                // Validation stamp name
                                $('#validation_stamp-owner-dialog-name').val(name);
                                // List of accounts
                                $('#validation_stamp-owner-dialog-owner').empty();
                                $('#validation_stamp-owner-dialog-owner')
                                    .append($("<option></option>")
                                        .attr("value", "")
                                        .text(loc('validation_stamp.owner.none')));
                                $.each(accounts, function (index, account) {
                                    var option = $("<option></option>")
                                        .attr("value", account.id)
                                        .text('{0} - {1}'.format(account.name, account.fullName));
                                    if (account.id == currentOwner) {
                                        option.attr('selected', 'selected');
                                    }
                                    $('#validation_stamp-owner-dialog-owner').append(option);
                                });
                            },
                            submitFn: function (closeFn) {
                                // Gets the selected owner
                                var owner = $('#validation_stamp-owner-dialog-owner').val();
                                // Removes the owner
                                if (owner == '') {
                                    AJAX.del({
                                        url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/owner'.format(
                                            project,
                                            branch,
                                            name
                                        ),
                                        loading: {
                                            el: $('#validation_stamp-owner-dialog-submit')
                                        },
                                        successFn: function (ack) {
                                            if (ack.success) {
                                                // Refreshes the validation stamps
                                                Template.reload('validation_stamps');
                                                // Closes the dialog
                                                closeFn();
                                            }
                                        }
                                    });
                                }
                                // Changes the owner
                                else {
                                    AJAX.put({
                                        url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/owner/{3}'.format(
                                            project,
                                            branch,
                                            name,
                                            owner
                                        ),
                                        loading: {
                                            el: $('#validation_stamp-owner-dialog-submit')
                                        },
                                        successFn: function (ack) {
                                            if (ack.success) {
                                                // Refreshes the validation stamps
                                                Template.reload('validation_stamps');
                                                // Closes the dialog
                                                closeFn();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }
	
	function deleteValidationStamp(project, branch, name) {
		Application.deleteEntity('project/{0}/branch/{1}/validation_stamp'.format(project,branch), name, '');
	}

	function validationStampImage (project, branch, stamp) {
	    return '<img class="tooltip-source" width="24" title="{2}" src="gui/project/{0}/branch/{1}/validation_stamp/{2}/image" />'.format(
               					project.html(),
               					branch.html(),
               					stamp.name.html()
               					);
	}
	
	function validationStampTemplate (project, branch) {
	    return Template.config({
	        url: 'ui/manage/project/{0}/branch/{1}/validation_stamp'.format(project,branch),
            preProcessingFn: function (stamps) {
                var count = stamps.length;
                $.each(stamps, function (index, stamp) {
                    if (index == 0) {
                        stamp.first = true;
                    }
                    if (index == count - 1) {
                        stamp.last = true;
                    }
                });
                return stamps;
            },
	        render: Template.asTableTemplate('validationStampTemplate')
	    });
	}
	
	function editImage () {
		$('#validation_stamp-image-form').toggle();
	}
	
	function editImageCancel() {
		$('#validation_stamp-image-form').hide();
	}
	
	return {
		createValidationStamp: createValidationStamp,
		deleteValidationStamp: deleteValidationStamp,
		updateValidationStamp: updateValidationStamp,
		validationStampTemplate: validationStampTemplate,
		validationStampImage: validationStampImage,
		editImage: editImage,
		editImageCancel: editImageCancel,
        up: up,
        down: down,
        changeOwner: changeOwner
	};
	
} ();