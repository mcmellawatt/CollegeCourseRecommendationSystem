/* javascript for history tab view */

(function () {

    function render(data) {
        console.log('render history view with', data);
    }

    // register our history functionality
    cs6310app.history = {
        render: render
    };
}());
