/* javascript for application */

(function () {

    // adjust tab classes to "select" tab with view id 'vid'
    // request data from the server for the given user's view
    // render the view
    function loadView(vid, user) {
        console.log('Loading', vid, 'view for user', user);
        var data = {
            user: user
        };

        $.postJSON(vid, data, function (resp) {
            cs6310app[resp.view].render(resp);
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

    // register our core functionality
    cs6310app.core = {
        loadView: loadView,
        view: view
    };
}());
