/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var completedTracker = [[],[],[]];
var index = 1;
var START = 1, END = 2, NUMBER = 3, DELIMITER = 4, BEFORE = 5, AFTER = 6;

$(document).ready(function() {
	for(var i=0; i<3; i++) {
		completedTracker[i][0] = 0;
	}

	setEmitters();
});

function setEmitters() {
	setTypeCheckedEmitter(index);
	setStartCheckedEmitter(index);
	setEndCheckedEmitter(index);
}

function setTypeCheckedEmitter(index) {
	var radios = document.getElementsByName('type_1_'+index);

	for(var i=0; i<radios.length; i++) {
		radios[i].onclick = function(){
		        var event = new CustomEvent("type"+index, {'detail':{'elementNumber':this.title,'index':index}});
		        document.dispatchEvent(event);
			document.removeEventListener('type'+index, typeFunction, false);
		};
		document.addEventListener('type'+index, typeFunction, false);
	}
}

var typeFunction = function(event) {
	completedTracker[0][event.detail.index-1] = 1;
	console.log('clicked on type button # '+event.detail.elementNumber+', with index # '+event.detail.index);
	console.log('compltedTracker[0]['+(event.detail.index-1)+'] is now = '+completedTracker[0][event.detail.index-1]);
};

function setStartCheckedEmitter(index) {
	var radios = document.getElementsByName('start_'+index);

	for(var i=0; i<radios.length; i++) {
		radios[i].onclick= function() {
			var event = new CustomEvent("start"+index, {'detail':{'index':index,'type':this.value,'section':START}});
			document.dispatchEvent(event);
		};
	}
	document.getElementsByName('start_'+NUMBER+'_'+index)[0].oninput = function() {
		var event = new CustomEvent("start"+index, {'detail':{'index':index,'type':NUMBER,'section':START}});
		document.dispatchEvent(event);
	};
	document.getElementsByName('start_'+DELIMITER+'_'+index)[0].oninput= function() {
		var event = new CustomEvent("start"+index, {'detail':{'index':index,'type':DELIMITER,'section':START}});
		document.dispatchEvent(event);
	};

	document.addEventListener('start'+index, setLimitsFunction, false);
}

function setEndCheckedEmitter(index) {
	var radios = document.getElementsByName('end_'+index);

	for(var i=0; i<radios.length; i++) {
		radios[i].onclick= function() {
			var event = new CustomEvent("end"+index, {'detail':{'index':index,'type':this.value,'section':END}});
			document.dispatchEvent(event);
		};
	}
	document.getElementsByName('end_'+NUMBER+'_'+index)[0].oninput = function() {
		var event = new CustomEvent("end"+index, {'detail':{'index':index,'type':NUMBER,'section':END}});
		document.dispatchEvent(event);
	};
	document.getElementsByName('end_'+DELIMITER+'_'+index)[0].oninput= function() {
		var event = new CustomEvent("end"+index, {'detail':{'index':index,'type':DELIMITER,'section':END}});
		document.dispatchEvent(event);
	};

	document.addEventListener('end'+index, setLimitsFunction, false);
}

var setLimitsFunction = function(event) {
	console.log('index = '+event.detail.index+', type = '+event.detail.type);
	var section = Number(event.detail.section);

	switch(Number(event.detail.type)) {
		case START:
			completedTracker[1][event.detail.index-1] = 1;
			break;
		case END:
			completedTracker[section][event.detail.index-1] = 1;
			break;
		case NUMBER:
			checkNumberDeliminter(NUMBER, Number(event.detail.index), section);
			break;
		case DELIMITER:
			checkNumberDeliminter(DELIMITER, Number(event.detail.index), section);
			break;
		case BEFORE:
			completedTracker[section][event.detail.index-1] = 1;
			break;
		case AFTER:
			completedTracker[section][event.detail.index-1] = 1;
			break;
	}

	console.log('compltedTracker['+section+']['+(event.detail.index-1)+'] is now = '+completedTracker[section][event.detail.index-1]);
};

function checkNumberDeliminter(type, index, tense) {
	var tense_text = (tense === START) ? 'start' : 'end';
	var checked = $('input[type=radio][name='+tense_text+'_'+index+'][value='+type+']').prop('checked');

	if(checked) {
		var text = $('input[type=text][name='+tense_text+'_'+type+'_'+index+']').val().length > 0;

		if(text){
			completedTracker[Number(tense)][Number(index)-1] = 1;
		}
		else {
			completedTracker[Number(tense)][Number(index)-1] = 0;
		}
	}
}
