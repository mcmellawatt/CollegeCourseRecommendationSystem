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

        view.append('<h2> Drag courses into priority order... </h2>');
        view.append('<p> Your top priority courses at top of list </p>');

        // TODO: find a better way of doing this...
        var stuff = [];
        stuff.push('<div id="courselist">');
        stuff.push('<ul>');
        p.courses.forEach(function (c) {
            stuff.push('<li> ' + c.tag + ' ' + c.name + ' </li>');
        });
        stuff.push('</ul>');
        stuff.push('</div>');
        view.append(stuff.join(''));
        $('#courselist').find('ul').sortable();

        view.append('<button id="sub-req-btn">Submit recommendation request</button>');
    }

    // register our courses functionality
    cs6310app.courses = {
        render: render
    };
}());
