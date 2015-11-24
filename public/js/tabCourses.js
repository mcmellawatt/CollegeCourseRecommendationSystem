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
            csv = p.courseOrderCsv,
            view = core().view('courses'),
            order = [],
            lookup = {};

        p.courses.forEach(function (c) {
            lookup[c.id] = c;
            if (!csv) {
                order.push(c.id);
            }
        });

        if (csv) {
            order = csv.split(',');
        }

        view.append('<h2> Drag courses into priority order... </h2>');
        view.append('<p> Your top priority courses at top of list </p>');

        // TODO: find a better way of doing this...?
        var stuff = [];
        stuff.push('<div id="course-list">');
        stuff.push('<ul>');

        order.forEach(function (id) {
            var c = lookup[id];

            stuff.push('<li>');
            stuff.push('<span class="course-id">');
            stuff.push(c.id);
            stuff.push('</span><b>');
            stuff.push(c.tag);
            stuff.push('</b> - ');
            stuff.push(c.name);
            stuff.push('</li>');
        });

        stuff.push('</ul>');
        stuff.push('</div>');
        view.append(stuff.join(''));
        $('#course-list').find('ul').sortable();

        view.append('<button id="sub-req-btn">Submit Recommendation Request</button>');
    }

    // called when our view is unloaded
    function unload(user) {
        console.log('UNLOADING Courses View...');
        var items = $('#course-list').find('li'),
            ids = [];

        items.each(function () {
            var li = $(this);
            var id = li.find('.course-id').text();
            ids.push(id);
        });

        var payload = {
            user: user,
            courseOrderCsv: ids.join(',')
        };

        $.postJSON('courses/store', payload, function (resp) {
            console.debug('courses/store returned', resp);
        });
    }

    // register our courses functionality
    cs6310app.courses = {
        render: render,
        unload: unload
    };
}());
