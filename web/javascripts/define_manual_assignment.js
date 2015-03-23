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
		option_counts.push([]);
		option_counts[i][0] = UNCHECKED;
	}

	initializeEmitters(manualCount);
});

function initializeEmitters(count) {
	for(var i=1; i<=count; i++) {
		var radios = getElementsByName("manual_type_"+i);
		for(var j=0; j<radios.length; j++) {
			radios[j].onclick(function () {
				var event = new CustomEvent("manual_"+i, {'detail':{'index':i, 'value':this.value}});
				document.dispatchEvent(event);
			});
		}
		document.addEventListener("manual_"+i, manualType, false);
	}
}

var manualType = function(event) {
	switch(event.detail.value) {
		case TEXT:
		case RADIO:
		case CHECKBOX:
	}
};