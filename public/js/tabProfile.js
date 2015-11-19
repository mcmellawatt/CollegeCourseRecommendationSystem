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

        view.append('<p>' + p.fullName + '</p>')
            .append('<p>' + p.numCoursesPreferred + '</p>');
    }

    // register our profile functionality
    cs6310app.profile = {
        render: render
    };
}());
