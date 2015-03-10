/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var completedTracker = [[],[],[]];
var index = 1;

$(document).ready(function() {
	for(var i=0; i<3; i++) {
		completedTracker[i][0] = 0;
	}
	setTypeCheckedEmitter(index);
	setStartCheckedEmitter(index);
});

function setTypeCheckedEmitter(index) {
	var radios = document.getElementsByName('type_1_'+index);

	for(var i=0; i<radios.length; i++) {
		radios[i].onchange = function(){
		       var event = new CustomEvent("type"+index, {'detail':{'elementNumber':this.title,'index':index}});
		       this.onclick = document.dispatchEvent(event);
			document.removeEventListener('type'+index, typeFunction, false);
		};
		document.addEventListener('type'+index, typeFunction, false);
	}
}

var typeFunction = function(event) {
	completedTracker[0][event.detail.index-1] = 1;
	console.log('clicked on type button # '+event.detail.elementNumber+', with index # '+event.detail.index);
	console.log('compltedTracker[0]['+(event.detail.index-1)+'] is now = '+completedTracker[0][event.detail.index-1]);
}
