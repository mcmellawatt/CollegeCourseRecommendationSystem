/* javascript for application core functionality */

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
            var cv = current.view,
                rv = resp.responseType;
            if (cv && cv !== rv) {
                cs6310app[cv].unload(user);
            }
            current.view = rv;
            cs6310app[rv].render(resp);
        });
    }

    function unloadView(user) {
        var cv = current.view;
        if (cv) {
            console.log('Unloading ' + cv + ' view for user', user);
            cs6310app[cv].unload(user);
        }
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

    // format the HTML for a course
    function htmlCourse(c) {
       var html = [];
        if (c.core) {
            html.push('<span class="star">*</span>');
        }
        html.push('<span class="tag">');
        html.push(c.tag);
        html.push('</span><span class="name">');
        html.push(c.name);
        html.push('</span>');
        return html.join('');
    }

    // register our core functionality
    cs6310app.core = {
        loadView: loadView,
        unloadView: unloadView,
        view: view,
        currentUser: currentUser,
        htmlCourse: htmlCourse
    };
}());
