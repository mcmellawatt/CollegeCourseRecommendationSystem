/* javascript for history tab view */

(function () {

    // shorthand reference to core module
    function core() {
        return cs6310app.core;
    }

    // render the history view with the given response data
    function render(resp) {
        console.log('render history view with', resp);

        var p = resp.payload,
            view = core().view('history');

        view.append('<h2> History View </h2>');
    }

    // register our history functionality
    cs6310app.history = {
        render: render
    };
}());
