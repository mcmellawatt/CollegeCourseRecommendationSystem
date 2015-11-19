/* javascript for courses tab view */

(function () {

    // shorthand reference to core module
    function core() {
        return cs6310app.core;
    }

    // render the courses view with the given response data
    function render(resp) {
        console.log('render courses view with', resp);

        var p = resp.payload,
            view = core().view('courses');

        view.append('<h2> Courses View </h2>');
    }

    // register our courses functionality
    cs6310app.courses = {
        render: render
    };
}());
