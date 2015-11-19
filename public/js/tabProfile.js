/* javascript for profile tab view */

(function () {

    function core() {
        return cs6310app.core;
    }

    function render(data) {
        console.log('render profile view with', data);

        var s = data.student,
            view = core().view('profile');


        view.append('<p>' + s.fullName + '</p>')
            .append('<p>' + s.numCoursesPreferred + '</p>');
    }

    // register our profile functionality
    cs6310app.profile = {
        render: render
    };
}());
