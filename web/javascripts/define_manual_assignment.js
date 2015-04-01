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
});

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
		var event = new CustomEvent('manual_'+index, {'detail':{'index':index, 'value':this.value}});
		document.dispatchEvent(event);
	};
}

var manualType = function(event) {
	if(+event.detail.value === TEXT)  {
		clearOptions(+event.detail.index);
	}
	else if(option_counts[+event.detail.index-1] === 0){
		addRadioOrCheck(event.detail.index, event.detail.value);
	}
};

function clearOptions(index) {
	while(option_counts[index-1] > 0) {
		option_counts[+index-1]--;
		var row = $('div[name='+index+'_option_'+option_counts[+index]+']');
		row.remove();
	}
}

function addRadioOrCheck(index, type) {
	var row = $("div[name=manual_"+(index)+"]");
	var newInput = "<div class='meta-col' name='"+index+"_option_"+option_counts[index]+
			"'><br><br><br>Option label: <input type='text' name='option_"+
			option_counts[index]+"'>";
	row.append(newInput);
	option_counts[index-1]++;
}