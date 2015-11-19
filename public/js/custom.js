
(function () {
    /* Our customization of jQuery, to add a postJSON() function */
    jQuery.extend({
        postJSON: function(url, data, callback) {
            return jQuery.ajax({
                type: "POST",
                url: url,
                data: JSON.stringify(data),
                success: callback,
                dataType: "json",
                contentType: "application/json",
                processData: false
            });
        }
    });

    /* One global variable on which to hang all our javascript */
    cs6310app = {};
}());
