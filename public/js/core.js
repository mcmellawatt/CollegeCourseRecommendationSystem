/* javascript for application */

(function () {

    function loadView(viewid, username) {
        console.log("Loading view", viewid, " for user ", username);
        var data = {
            user: username
        };

        $.postJSON(viewid, data, function (response) {
            cs6310app[response.view].render(response);
        });
    }

    function view(vid) {
        var view = $('#view');
        view.empty();
        view.removeClass('profile courses history');
        view.addClass(vid);
        return view;
    }

    // register our core functionality
    cs6310app.core = {
        loadView: loadView,
        view: view
    };
}());
