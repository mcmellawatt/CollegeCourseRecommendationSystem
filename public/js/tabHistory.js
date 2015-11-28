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
            view.append('<p>' + nsol + ' Recommendation' + ss + ':</p>');

            var html = [];
            html.push('<table class="history-info">');
            solns.forEach(function (sol) {
                genSolutionRow(html, sol);
            });
            view.append(html.join(''));
        }
    }

    function genSolutionRow(html, sol) {
        html.push('<tr class="ts">');
        html.push('<td>');
        html.push(sol.timestamp);
        html.push('</td>');
        html.push('</tr>');

        html.push('<tr class="npref">');
        html.push('<td>');
        html.push('# Courses Preferred: <b>');
        html.push(sol.numCoursesPreferred);
        html.push('</b>');
        html.push('</td>');
        html.push('</tr>');

        html.push('<tr class="npref">');
        html.push('<td>');
        html.push('Recommended Courses:');
        html.push('</td>');
        html.push('</tr>');

        sol.recommended.forEach(function (rec) {
            html.push('<tr class="course">');
            html.push('<td>');
            html.push('<b>' + rec.tag + '</b> ' + rec.name);
            html.push('</td>');
            html.push('</tr>');
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
