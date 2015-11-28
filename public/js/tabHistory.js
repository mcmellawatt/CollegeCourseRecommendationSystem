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
            s = p.student,
            solns = p.solutions,
            nsol = solns.length,
            ss = nsol === 1 ? '' : 's',
            view = core().view('history');

        view.append('<h2> History View </h2>');

        if (!nsol) {
            view.append('<p>(No Recommendations)</p>');
        } else {
            var html = [];
            html.push('<table class="history-info">');
            solns.forEach(function (sol) {
                genSolutionRow(html, sol);
            });
            view.append(html.join(''));
        }
    }

    function genSolutionRow(html, sol) {

        var drvd = sol.derived,
            ts = sol.timestamp,
            clsDerived = drvd ? ' derived' : '',
            time = drvd ? ts + ' (derived)' : ts;


        function mkRow(cls, value) {
            html.push('<tr class="');
            html.push(cls + clsDerived);
            html.push('"><td>');
            html.push(value);
            html.push('</td></tr>');
        }

        mkRow('ts', time);
        mkRow('npref', '# Courses Preferred: <b>' + sol.numCoursesPreferred + '</b>');
        mkRow('npref', 'Recommended Courses:');
        sol.recommended.forEach(function (rec) {
            mkRow('course', '<b>' + rec.tag + '</b> ' + rec.name);
        });
    }

    // called when our view is unloaded
    function unload() {
        // nothing to do here
    }


    // register our history functionality
    cs6310app.history = {
        render: render,
        unload: unload
    };
}());
