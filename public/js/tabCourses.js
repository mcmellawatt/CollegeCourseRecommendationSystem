/* javascript for courses tab view */

(function () {

    var imsgEdit = 'Drag courses into priority order (highest at top)',
        imsgNoEdit = 'Request submitted... awaiting results...',
        pollTask,
        cachedNcp,
        saveOrder = true;

    // shorthand reference to core module
    function core() {
        return cs6310app.core;
    }

    // make number-of-preferred-courses widget
    function mkNcp(b) {
        return b ? '<input type="text" value="' + cachedNcp + '">'
                 : '<h3>' + cachedNcp + '</h3>';
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

        ncp.empty();
        ncp.append(mkNcp(b));

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
            ul.append('<li class="result course">' + core().htmlCourse(r) + '</li>');
        });

        btn.prop('disabled', false);
        btn.val('New Query');
        btn.off().on('click', backToEditing);

         function backToEditing() {
            core().loadView('courses', user);
        }

        // we're subverting the list of available courses, so we don't want
        // the save-order functionality to kick in...
        saveOrder = false;
        console.debug('UPDATED');
    }



    // render the courses view with the given response data
    function render(resp) {
        console.log('render courses view with', resp);

        var ru = resp.user,
            rp = resp.payload,
            s = rp.student,
            crs = rp.courses,
            csv = s.courseOrderCsv,
            editable = !s.batch,
            view = core().view('courses'),
            order = [],
            lookup = {},
            imsg = editable ? imsgEdit : imsgNoEdit;

        cachedNcp = s.numCoursesPreferred;

        view.toggleClass('editable', editable);
        saveOrder = editable;

        // stuff courses into a hash lookup
        crs.forEach(function (c) {
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

        var html = [];
        html.push('<div id="ncp">');
        html.push(mkNcp(editable));
        html.push('</div>');
        view.append(html.join(''));

        view.append('<h2 id="crshdr"> Available Courses: </h2>');
        view.append('<p id="imsg" class="instruct">' + imsg + '</p>');

        html = [];
        html.push('<div id="course-list">');
        html.push('<ul>');

        order.forEach(function (id) {
            var c = lookup[id],
                hc = core().htmlCourse(c);

            html.push('<li class="course">');
            html.push('<span class="course-id">');
            html.push(c.id);
            html.push('</span>');
            html.push(hc);
            html.push('</li>');
        });

        html.push('</ul>');
        html.push('</div>');
        view.append(html.join(''));

        view.append('<input type="button" id="subreq">');

        $('#subreq').click(function () {
            console.log('Submitting Request...');
            cachedNcp = $('#ncp').find('input').val();
            $.postJSON('courses/submit', genStorePayload(ru), function (resp) {
                console.debug('courses/submit returned', resp);
                pollForResult(ru, resp.payload.batch);
            });
            enableEditing(false);
        });

        enableEditing(editable);
        if (!editable) {
            pollForResult(ru, s.batch);
        }
    }

    // called when our view is unloaded
    function unload(user) {
        console.log('UNLOADING Courses View...');
        if (saveOrder) {
            $.postJSON('courses/store', genStorePayload(user), function (resp) {
                console.debug('courses/store returned', resp);
            });
        }
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
            ids = [];

        items.each(function () {
            var li = $(this);
            var id = li.find('.course-id').text();
            ids.push(id);
        });

        return {
            user: user,
            courseOrderCsv: ids.join(','),
            numCoursesPreferred: cachedNcp
        };
    }


    // register our courses functionality
    cs6310app.courses = {
        render: render,
        unload: unload
    };
}());
