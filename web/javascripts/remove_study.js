/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



$(document).ready(function() {
	var submit = document.getElementsByName('delete')[0];
	submit.onclick = function(e) {
		var result = window.confirm("Are you sure you want to delete this study?\n This action cannot be undone.");
		if(result === false) { e.preventDefault(); }
	};
});