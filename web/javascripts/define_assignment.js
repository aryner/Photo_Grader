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
	checkFieldMatching();
});

function setEmitters() {
	setTypeCheckedEmitter(index);
	setLimitsEmitter(index);
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

	checkForRows(event.detail.index);
};

function setLimitsEmitter(index) {
	var radios = Array.prototype.slice.call(document.getElementsByName('start_'+index));
	radios = radios.concat(Array.prototype.slice.call(document.getElementsByName('end_'+index)));

	for(var i=0; i<radios.length; i++) {
		radios[i].onclick = function() {
			var side = (this.name.indexOf('start')>-1 ? START : END);
			var side_text = (side === START) ? 'start' : 'end';
			var event = new CustomEvent(side_text+index, {'detail':{'index':index,'type':this.value,'section':side}});
			document.dispatchEvent(event);
		};
	}

	setTextBoxLimitsEmitter(START,'start',index);
	setTextBoxLimitsEmitter(END,'end',index);

	document.addEventListener('start'+index, setLimitsFunction, false);
	document.addEventListener('end'+index, setLimitsFunction, false);
}

function setTextBoxLimitsEmitter(side, side_text, index) {
	document.getElementsByName(side_text+'_'+NUMBER+'_'+index)[0].oninput = function() {
		this.value = this.value.replace(/[^0-9]/,'');
		var event = new CustomEvent(side_text+index, {'detail':{'index':index,'type':NUMBER,'section':side}});
		document.dispatchEvent(event);
	};
	document.getElementsByName(side_text+'_'+DELIMITER+'_'+index)[0].oninput= function() {
		var event = new CustomEvent(side_text+index, {'detail':{'index':index,'type':DELIMITER,'section':side}});
		document.dispatchEvent(event);
	};
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
	checkForRows(event.detail.index);
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

function checkForRows(index) {
	var completed = completedTracker[0][index-1] + completedTracker[1][index-1] + completedTracker[2][index-1];

	if(completed === 3) {
		console.log(1);
		if(window.index === index) {
			window.index++;
			for(var i=0; i<3; i++) {
				completedTracker[i][index] = 0;
			}

			makeRow();

			setEmitters();
		}
	}
	else {
		var oldIndex = window.index;
		window.index = index;

		removeRows(oldIndex);
	}
}

function makeRow() {
	var newRow = "<div class='row_"+index+"'><div class='newRow'></div>"+
		     "<div class='meta-row'><div class='meta-col'>"+
		     "<h4>Which type of meta-data?</h4>"+
		     radioFields()+
		     "</div><div class='meta-col'>"+
		     "<h4>Where does this section start?</h4>"+
//		     "<input type='radio' name='start_"+index+"' value='"+START+"'> Begining of the file name<br>"+
		     "<input type='radio' name='start_"+index+"' value='"+NUMBER+"' > After "+
		     "<input type='text'  class='small_text_box' name='start_"+NUMBER+"_"+index+"'> characters (0 is the start, 1 is after the first character, etc...)<br>"+
		     "<input type='radio' name='start_"+index+"' value='"+DELIMITER+"' > After "+
		     "<input type='text' class='small_text_box' name='start_"+DELIMITER+"_"+index+"'> (Not including the delimiter character)<br>"+
		     "<input type='radio' name='start_"+index+"' value='"+AFTER+"'> Right after the end of the previous section"+
		     "</div><div class='meta-col'>"+
		     "<h4>Where does this section end?</h4>"+
		     "<input type='radio' name='end_"+index+"' value='"+END+"'> The end of the file name (not including the extension)<br>"+
		     "<input type='radio' name='end_"+index+"' value='"+NUMBER+"'> After "+
		     "<input type='text' name='end_"+NUMBER+"_"+index+"' class='small_text_box'> characters from the start section"+
		     " (this number is the same as the length of this section in characters)<br>"+
		     "<input type='radio' name='end_"+index+"' value='"+DELIMITER+"'> Before "+
		     "<input type='text' name='end_"+DELIMITER+"_"+index+"' class='small_text_box'> "+
		     " (Not including the delmiter character)<br>"+
		     "<input type='radio' name='end_"+index+"' value='"+BEFORE+"'> Right before the start of the next section"+
		     "</div>"+
			
		     "</div></div>";
	$('div.meta-row-container').append(newRow);
}

function radioFields() {
	var fields = document.getElementsByName("type_1_1");
	var buttons = "";

	for(var i=0; i<fields.length; i++) {
		buttons += "<input type='radio' name='type_1_"+index+"' value='"+fields[i].value+"'>"+
			(i>0?fields[i].value:'Not meta-data (use to help break name into sections)')+"<br>";
	}

	return buttons;
}

function removeRows(oldIndex) {
	// - remove the emitters above the new index
	// - remove the rows above the new index
	for(var i=oldIndex; i>index; i--) {
		document.removeEventListener('start'+i, setLimitsFunction, false);
		document.removeEventListener('end'+i, setLimitsFunction, false);

		$('div.row_'+i).remove();
	}
}

function checkFieldMatching() {
	$('input[type=text][name=exampleInput]').on('input', function() {
		document.getElementsByName("example")[0].innerHTML = sectionText(this.value);
	});
}

function sectionText(text) {
	if(index === 1) return text;
	var types = [];
	var starts = [];
	var ends = [];

	for(var i=1; i<index; i++) {
		types[i-1] = $('input[name=type_1_'+i+']:checked').val();
		starts[i-1] = $('input[name=start_'+i+']:checked').val();
		ends[i-1] = $('input[name=end_'+i+']:checked').val();
	}
	
	return processText(text, starts, ends, getColorCodes(types));
}

function processText(text, starts, ends, colorCodes) {
	var currIndex = 0;
	var processedText = "";
	for(var i=0; i<starts.length; i++) {
		currIndex = getStartIndex(currIndex, starts[i], text, (i+1));
		console.log('currIndex = '+currIndex);
	}

	return processedText;
}

function getStartIndex(currIndex, start, text, index) {
	switch(+start) {
		case START:
		case AFTER:
			return currIndex;
		case NUMBER:
			return currIndex + Number($('input[name=start_'+NUMBER+'_'+index+']').val());
		case DELIMITER:
			return getDelimIndex(currIndex, text, index);
	}
	return null;
}

function getDelimIndex(currIndex, text, index) {
	var delimiter = $('input[name=start_'+DELIMITER+'_'+index+']').val();
	return text.indexOf(delimiter,currIndex);
}

function getColorCodes(types) {
	var colorCodes = [];
	for(var i=0; i<types.length; i++) {
		if(types[i] === '') { colorCodes[i] = ''; continue; }
		var element = document.getElementsByName(types[i]);
		colorCodes[i] = "background='"+element[0].style.background+"'";
	}

	return colorCodes;
}