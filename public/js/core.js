/* javascript for application */

(function () {

    // remember our state
    var current = {
        view: null,
        user: null
    };

    // adjust tab classes to "select" tab with view id 'vid'
    // request data from the server for the given user's view
    // render the view
    function loadView(vid, user) {
        console.log('Loading', vid, 'view for user', user);
        current.user = user;

        var data = {
            user: user
        };

        $.postJSON(vid, data, function (resp) {
            if (current.view) {
                cs6310app[current.view].unload(user);
            }
            current.view = resp.view;
            cs6310app[current.view].render(resp);
        });
    }

    // return jQuery selection on the (emptied) view div, classed appropriately
    function view(vid) {
        var view = $('#view'),
            tabs = $('#app-ctrl').find('.tab'),
            tab = $('#tab-' + vid);
        view.empty();
        view.removeClass('profile courses history');
        view.addClass(vid);
        tabs.removeClass('current');
        tab.addClass('current');

        return view;
    }

    // returns the current user
    function currentUser() {
        return current.user;
    }

    // register our core functionality
    cs6310app.core = {
        loadView: loadView,
        view: view,
        currentUser: currentUser
    };
}());
