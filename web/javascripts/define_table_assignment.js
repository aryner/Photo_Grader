/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function() {
	var excelCount = Number($('input[name=excelCount]').val());
	var csvCount = Number($('input[name=csvCount]').val());
	setTableTextLimits(excelCount,csvCount);

	$(':Submit[value=Submit]').click(function(e) {
		var errors = getTableErrors(excelCount, csvCount);
		if(errors.length > 0) {
			console.log('error in table assignment');
			console.log('errors = '+errors);
			console.log('errros.length = '+errors.length);
			e.preventDefault();

			var msg = "";
			for(var i=0; i<errors.length; i++) {
				msg += errors[i];
			}
			var div = document.getElementsByClassName('errorDiv');
			div[0].innerHTML = msg;
		}
	});
});

function setTableTextLimits(excelCount,csvCount) {
	if(excelCount > 0) {
		for(var i=0; i<excelCount+1; i++) {
			setTextLimit('excel_column_'+i);
		}
	}
	if(csvCount > 0) {
		for(var i=0; i<csvCount+1; i++) {
			setTextLimit('csv_column_'+i);
		}
	}
}

function setTextLimit(name) {
	$('input[name='+name+']').on('input', function() {
		if(this.value.length > 30) {
			this.value = this.value.substring(0,30);
		}
	});
}

function getTableErrors(excelCount, csvCount) {
	var errors = [];
	if(excelCount > 0) {
		checkForIdentifier("excel",errors);
		checkForColNames(excelCount, "excel", errors);
	}
	if(csvCount > 0) {
		checkForIdentifier("csv",errors);
		checkForColNames(excelCount, "csv", errors);
	}
	return errors;
}

function checkForIdentifier(type, errors) {
	if ($('input[name='+type+'Identifier]:checked').val() === undefined) {
		errors.push("You must select which piece of meta-data will be the "+type+" identifier<br>");
	}
}

function checkForColNames(count, type, errors) {
	for(var i=0; i<=count; i++) {
		if(invalidColumnName($('input[name='+type+'_column_'+i+']').val())) {
			errors.push("You must enter a valid column name for each "+type+" column.  Names must be only letters and numbers and must start with a letter<br>");
			break;
		}
	}
}

function invalidColumnName(name) {
	if(name.length === 0 || (!name.match(/^[a-zA-z]/) || name.match(/[^a-zA-Z0-9]/))) {
		return true;
	}
	return false;
}
