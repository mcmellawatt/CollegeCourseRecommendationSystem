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

        // TODO: find a better way of doing this...
        var stuff = [];
        stuff.push('<ul id="courselist">');
        p.courses.forEach(function (c) {
            stuff.push('<li> ' + c.tag + ' ' + c.name + ' </li>');
        });
        view.append(stuff.join(''));
        $('#courselist').sortable();
    }

    // register our courses functionality
    cs6310app.courses = {
        render: render
    };
}());
