/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function() {
	$('input[type=submit][name=delete]').click(function(e) {
		var result = window.confirm("Are you sure you want to delte this category from the program?\n\nIt can't be undone; all data from this category will be lost");
		if(result === false) { e.preventDefault(); }
	});
});
