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
            view = core().view('profile');

        view.append('<h2> Profile for ' + p.fullName + ' </h2>');

        var t = [];
        t.push('<h3>Courses Taken</h3><table>');
        p.coursesTaken.forEach(function (c) {
            t.push('<tr><td>');
            if (c.core) {
                t.push('*');
            }
            t.push('</td><td><b>');
            t.push(c.tag);
            t.push('</b></td><td>');
            t.push(c.name);
            t.push('<span class="abbrev"> (');
            t.push(c.abbrev);
            t.push(') </span></td></tr>');
        });
        t.push('</table>');
        view.append(t.join(''));

        t = ['<p>'];
        t.push('Credits Earned: <b>');
        t.push(p.creditsEarned);
        t.push('</b></p>');
        view.append(t.join(''));
    }

    // called when our view is unloaded
    function unload() {

    }


    // register our profile functionality
    cs6310app.profile = {
        render: render,
        unload: unload
    };
}());
