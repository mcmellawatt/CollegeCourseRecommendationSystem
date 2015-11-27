/* javascript for courses tab view */

(function () {

    var imsgEdit = 'Drag courses into priority order (highest at top)',
        imsgNoEdit = 'Request submitted... awaiting results...',
        pollTask;

    // shorthand reference to core module
    function core() {
        return cs6310app.core;
    }

    function enableEditing(b) {
        var ncp = $('#ncp'),
            imsg = $('#imsg'),
            uls = $('#course-list').find('ul'),
            btn = $('#subreq'),
            btntxt = b ? 'Submit Request' : 'Submitted...',
            msgtxt = b ? imsgEdit : imsgNoEdit;

        uls.sortable();
        if (!b) {
            uls.sortable('disable');
        }

        $('#view').toggleClass('editable', b);
        imsg.html(msgtxt);
        ncp.prop('disabled', !b);
        btn.prop('disabled', !b);
        btn.val(btntxt);
    }

    function pollForResult(user, batch) {
        console.debug("NEED to poll the server for our batch result", batch);

        function schedule() {
            pollTask = setTimeout(doPoll, 2000);
        }

        function doPoll() {
            console.debug('doPOLL for', user, batch);

            $.postJSON('courses/poll', genPollPayload(user, batch), function (resp) {
                console.debug('courses/poll returned', resp);
                var p = resp.payload;

                if (p) {
                    console.debug('TIME to post the RESULTS', p.recommended);
                    updateWithResults(user, p.recommended);
                } else {
                    schedule();
                }
            });
        }

        schedule();
    }

    function cancelPollTask() {
        if (pollTask) {
            clearTimeout(pollTask);
            pollTask = null;
            console.debug('poll task canceled');
        }
    }

    function updateWithResults(user, rec) {
        var ul = $('#course-list').find('ul'),
            btn = $('#subreq');

        $('#crshdr').html('Recommended Courses:');
        $('#imsg').html('Picked lovingly for you ...');

        ul.empty();
        rec.forEach(function (r) {
            ul.append('<li class="result">' + r.tag + ' ' + r.name + '</li>');
        });

        btn.prop('disabled', false);
        btn.val('New Query');
        btn.off().on('click', backToEditing);

         function backToEditing() {
            core().loadView('courses', user);
        }

        console.debug('UPDATED');
    }



    // render the courses view with the given response data
    function render(resp) {
        console.log('render courses view with', resp);

        var u = resp.user,
            p = resp.payload,
            csv = p.courseOrderCsv,
            editable = !p.batch,
            numCP = p.numCoursesPreferred,
            view = core().view('courses'),
            order = [],
            lookup = {},
            imsg = editable ? imsgEdit : imsgNoEdit;

        view.toggleClass('editable', editable);


        // stuff courses into a hash lookup
        p.courses.forEach(function (c) {
            lookup[c.id] = c;
            if (!csv) {
                order.push(c.id);
            }
        });

        // get pre-stored course order if it is available
        if (csv) {
            order = csv.split(',');
        }

        // construct the view
        view.append('<h2> Number of Courses Preferred: </h2>');
        view.append('<input id="ncp" type="text" value="' + numCP + '">');

        view.append('<h2 id="crshdr"> Available Courses: </h2>');
        view.append('<p id="imsg" class="instruct">' + imsg + '</p>');

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


        view.append('<input type="button" id="subreq">');

        $('#subreq').click(function () {
            console.log('Submitting Request...');
            $.postJSON('courses/submit', genStorePayload(u), function (resp) {
                console.debug('courses/submit returned', resp);
                enableEditing(false);
                pollForResult(u, resp.payload.batch);
            });
        });

        enableEditing(editable);
        if (!editable) {
            pollForResult(u, p.batch);
        }
    }

    // called when our view is unloaded
    function unload(user) {
        console.log('UNLOADING Courses View...');
        $.postJSON('courses/store', genStorePayload(user), function (resp) {
            console.debug('courses/store returned', resp);
        });
        cancelPollTask();
    }

    function genPollPayload(user, batch) {
        return {
            user: user,
            batch: batch
        }
    }

    function genStorePayload(user) {
        var items = $('#course-list').find('li'),
            ncp = $('#ncp').val(),
            ids = [];

        items.each(function () {
            var li = $(this);
            var id = li.find('.course-id').text();
            ids.push(id);
        });

        return {
            user: user,
            courseOrderCsv: ids.join(','),
            numCoursesPreferred: ncp
        };
    }


    // register our courses functionality
    cs6310app.courses = {
        render: render,
        unload: unload
    };
}());
