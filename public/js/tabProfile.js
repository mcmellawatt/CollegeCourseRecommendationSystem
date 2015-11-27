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

        var html = [];
        html.push('<h3>Courses Taken</h3><table>');
        t.coursesTaken.forEach(function (c) {
            html.push('<tr><td>');
            if (c.core) {
                html.push('*');
            }
            html.push('</td><td><b>');
            html.push(c.tag);
            html.push('</b></td><td>');
            html.push(c.name);
            html.push('<span class="abbrev"> (');
            html.push(c.abbrev);
            html.push(') </span></td></tr>');
        });
        html.push('</table>');
        view.append(html.join(''));

        html = ['<p>'];
        html.push('Credits Earned: <b>');
        html.push(t.creditsEarned);
        html.push('</b></p>');
        view.append(html.join(''));
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
