AJS.toInit(function ($) { // this closure helps us keep our variables to ourselves.
// This pattern is known as an "iife" - immediately invoked function expression
	console.log("in AJS");
    // form the URL
    var url = AJS.contextPath() + "/rest/xproduct-admin/1.0.4/";
 
    // wait for the DOM (i.e., document "skeleton") to load. This likely isn't necessary for the current case,
    // but may be helpful for AJAX that provides secondary content.
    $(document).ready(function() {
    	console.log("ready function");
        // request the config information from the server
        $.ajax({
            url: url,
            dataType: "json"
        }).done(function(config) { // when the configuration is returned...
            // ...populate the form.
            $("#name").val(config.name);
            $("#time").val(config.time);
        });
    });
    function updateConfig() {
    	console.log("update function");
    	  AJS.$.ajax({
    	    url: url, /*baseUrl + "/rest/xproduct-admin/1.0.4/",*/
    	    type: "PUT",
    	    contentType: "application/json",
    	    data: '{ "name": "' + AJS.$("#name").attr("value") + '", "time": ' +  AJS.$("#time").attr("value") + ' }',
    	    processData: false
    	  });
    	}
    AJS.$("#admin").submit(function(e) {
    	console.log("in submit");
        e.preventDefault();
        updateConfig();
    });
})(AJS.$ || jQuery);