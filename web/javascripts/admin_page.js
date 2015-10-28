/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
	var user_count = document.getElementsByName('user_count')[0].value;
	setAdminChangeListeners(Number(user_count));

	var submit = document.getElementsByName('makeChanges')[0];
	submit.onclick = function(e) {
		var result = window.confirm("Are you sure you want to make these changes?");
		if(result === false) { e.preventDefault(); }
	};
});

function setAdminChangeListeners(user_count) {
	for(var i=0; i<user_count; i++) {
		setAdminChangeListener(i);
	}
}

function setAdminChangeListener(index) {
	var adminCheck = document.getElementsByName('admin_'+index)[0];
	var user_name = document.getElementsByName('name_'+index)[0].value;
	adminCheck.onclick = function(e) {
		var result = window.confirm("Are you sure you want to change "+user_name+"'s admin status?");
		if(result === false) { e.preventDefault(); }
	};
}
