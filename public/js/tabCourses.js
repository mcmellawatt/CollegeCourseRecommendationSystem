/* javascript for courses tab view */

(function () {

    function render(data) {
        console.log('render courses view with', data);
    }

    // register our courses functionality
    cs6310app.courses = {
        render: render
    };
}());
