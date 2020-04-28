import React from 'react';
import * as pathbrowser from 'path-browserify';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import { Routes, HomeRoute, LoginRoute, LogoutRoute, PostRoute, FilteredPostsRoute, NotFoundRoute } from './routes';

/** I'm literally flabbergasted that I had to do this... :o **/
function removeDamnHttp (strToFix) {
    return strToFix.replace(/http:\/+/, '/');
}

/** Returns from dotenv: REACT_APP_BASENAME=$npm_package_homepage **/
function find_base_name () {
    var base_name = process.env.REACT_APP_BASENAME;

    // (OPTIONAL) Check package.json for 'homepage'
    if (!base_name.length > 0) {
        return null;
    }

    // react-router will be pissed if the basename ends in a /
    if (base_name !== '/' && base_name.endsWith('/')) {
        base_name = base_name.slice(0, base_name.length - 1);
    }

    return removeDamnHttp(base_name);
}

/** Apply the route basename to a path **/
export class RouteURL {
    constructor (route_path) {
        this.path = route_path;
        this.basename = find_base_name();
    }

    get () {
        if (this.basename === null) {
            return this.path;
        }

        let theRoute = pathbrowser.join(this.basename, this.path);
        theRoute = removeDamnHttp(theRoute);

        return theRoute;
    }
}

/** Apply the route basename to a named route in routes/Routes
 *  An invalid, non-existent named route gens a path for '/'
 **/
export class FindRoutesURL extends RouteURL {
    constructor (find_route_name) {
        var path = '/';
        const route = Routes.filter(r => r.name === find_route_name);

        if (route.length !== 0) {
            path = route[0].path;
        }

        super(path);
    }
}

export default class AppRouter extends React.Component {
    render () {
        return (
            <div style={{ height: '100%' }}>
                <BrowserRouter basename={find_base_name()}>
                    <Switch>
                        <Route exact path="/">
                            <HomeRoute />
                        </Route>
                        <Route path="/login/" component={LoginRoute} />
                        <Route path="/logout/" component={LogoutRoute} />
                        <Route path="/post/:postID" component={PostRoute} />
                        <Route path="/posts/:year/:month" component={FilteredPostsRoute} />
                        <Route component={NotFoundRoute} />
                    </Switch>
                </BrowserRouter>
            </div>
        );
    }
}
