/* javascript for profile tab view */

(function () {

    // shorthand reference to core module
    function core() {
        return cs6310app.core;
    }

    // render the profile view with the given response data
    function render(resp) {
        console.log('render profile view with', resp);

        var p = resp.payload,
            s = p.student,
            t = p.transcript,
            view = core().view('profile');

        view.append('<h2> Profile for ' + s.fullName + ' </h2>');
        view.append('<h3>Courses Taken</h3>');

        var html = ['<table>'];
        t.coursesTaken.forEach(function (c) {
            html.push('<tr class="course"><td>');
            html.push(core().htmlCourse(c));
            html.push('</td></tr>');
        });
        html.push('</table>');
        view.append(html.join(''));

        html = ['<p>'];
        html.push('Credits Earned: <b>');
        html.push(t.creditsEarned);
        html.push('</b></p>');
        view.append(html.join(''));

        view.append('<p class="footnote">Foundational courses marked with' +
                    '<span class="bigger">*</span></p>');
    }

    // called when our view is unloaded
    function unload() {
        // we don't have anything to do here
    }

    // register our profile functionality
    cs6310app.profile = {
        render: render,
        unload: unload
    };
}());
