/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var option_counts = [];
var UNCHECKED = 0;
var TEXT = 1;
var RADIO = 2;
var CHECKBOX = 3;

$(document).ready(function() {
	var manualCount = Number($('input[name=manualCount]').val());
	for(var i=0; i<manualCount; i++) {
		option_counts.push(0);
	}

	initializeEmitters(manualCount);

	$(':Submit[value=Submit]').click(function(e) {
		var errors = getManualErrors();
		if(errors.length > 0) {
			console.log('error in manual assignment');
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

function getManualErrors() {
	var errors = [];

	//check each radio group
	for(var i=0; i<option_counts.length; i++) {
		if(!oneChecked(i)) {
			errors.push('You must select either text, radio button, or check box for each manual option, check row '+(i+1)+'<br>');
		}
		else if(!$('input[type=radio][name=manual_type_'+(i+1)+'][value='+TEXT+']').prop('checked')) {
			errors = (optionLabelErrorsCheck(errors, i));
		}
	}

	return errors;
}

function optionLabelErrorsCheck(errors, i) {
	return uniqueOptionsCheck(validOptionsCheck(atLeastOneOption(errors, i),i),i);
}

function atLeastOneOption(errors, i) {
	var input = $('input[type=text][name='+i+'_option_0]').val();
	if(input === undefined || input === '') {
		errors.push('Each manual radio or checkbox selection must have at least one option label, check row '+(i+1)+'<br>');
	}

	return errors;
}

function validOptionsCheck(errors, i) {
	var option_labels = $('input[type=text][title='+i+']');
	for(var j=0; j<option_labels.length; j++) {
		if(!validOptionLabelName(option_labels[j].value)) {
			errors.push("Option labels must start with a letter and only contain letters or numbers, check row "+(i+1)+'<br>');
			break;
		}
	}

	return errors;
}

function uniqueOptionsCheck(errors, i) {
	var option_labels = $('input[type=text][title='+i+']');
	for(var j=0; j<option_labels.length-1; j++) {
		for(var k=j+1; k<option_labels.length; k++) {
			if(option_labels[j].value.toLowerCase().trim() === option_labels[k].value.toLowerCase().trim()) {
				errors.push("Option labels for the same field must be unique, check row "+(i+1)+'<br>');
				break;
			}
		}
	}

	return errors;
}

function oneChecked(index) {
	return $('input[type=radio][name=manual_type_'+(index+1)+'][value='+TEXT+']').prop('checked')
		|| $('input[type=radio][name=manual_type_'+(index+1)+'][value='+RADIO+']').prop('checked')
		|| $('input[type=radio][name=manual_type_'+(index+1)+'][value='+CHECKBOX+']').prop('checked');
}

function validOptionLabelName(name) {
	if(name.length > 0 && (!name.match(/^[a-zA-z]/) || name.match(/[^a-zA-Z0-9]/))) 
		return false;
	return true;
}

function initializeEmitters(count) {
	for(var i=1; i<=count; i++) {
		var radios = document.getElementsByName("manual_type_"+i);
		for(var j=0; j<radios.length; j++) {
			setOnClick(radios[j], i);
		}
		document.addEventListener('manual_'+i, manualType, false);
	}
}

function setOnClick(radio, index) {
	radio.onclick = function () {
		var event = new CustomEvent('manual_'+index, {'detail':{'index':index-1, 'value':this.value}});
		document.dispatchEvent(event);
	};
}

var manualType = function(event) {
	if(+event.detail.value === TEXT)  {
		clearOptions(+event.detail.index);
	}
	else if(option_counts[+event.detail.index] === 0){
		addRadioOrCheck(event.detail.index);
	}
};

function clearOptions(index, downTo) {
	downTo = typeof downTo !== 'undefined' ? downTo : 0;
	while(option_counts[index] > downTo) {
		option_counts[+index]--;
		var row = $('div[name='+index+'_option_'+option_counts[+index]+']');
		row.remove();
	}
}

function addRadioOrCheck(index) {
	var row = $("div[name=manual_"+(+index+1)+"]");
	var newInput = "<div class='option-col' name='"+index+"_option_"+option_counts[index]+
			"'><br><br><br>Option label: <input type='text' name='"+(index)+"_option_"+
			option_counts[index]+"' title='"+index+"'>";
	row.append(newInput);
	option_counts[+index]++;
	addRadioOrCheckListener(+index, (+option_counts[index])-1);
}

function addRadioOrCheckListener(index,count) {
	var inputs = $('input[type=text][name='+(+index)+'_option_'+count+']');
	inputs[0].oninput = function() {
		var index = Number(this.name.substring(0,this.name.indexOf("_")));
		var count = Number(this.name.substring(this.name.lastIndexOf("_")+1));

		if(this.value.length > 0 && this.value !== '') {
			if((count+1) === option_counts[index]) {
				addRadioOrCheck(index);
			}
			if(this.value.length>30) {
				this.value = this.value.substring(0,30);
			}
		}
		else {
			clearOptions(index,count+1);
		}
	};
}
