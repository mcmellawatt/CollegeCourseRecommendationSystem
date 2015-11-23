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

        view.append('<h2> Profile for ' + p.fullName + ' </h2>')
            .append('<p> Number of preferred courses: ' + p.numCoursesPreferred + '</p>');

        view.append('<p> ( more information here ) </p>');
    }

    // register our profile functionality
    cs6310app.profile = {
        render: render
    };
}());
