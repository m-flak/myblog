import React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import { Routes, HomeRoute, LoginRoute, NotFoundRoute } from './routes';

/** Returns from dotenv: REACT_APP_BASENAME=$npm_package_homepage **/
function find_base_name() {
    var base_name = process.env.REACT_APP_BASENAME;

    // (OPTIONAL) Check package.json for 'homepage'
    if (!base_name.length > 0) {
        return null;
    }
    return base_name;
}

/** Apply the route basename to a path **/
export class RouteURL {
    constructor(route_path) {
        this.path = route_path;
        this.basename = find_base_name();
    }

    get() {
        if (this.basename === null) {
            return this.path;
        }
        var url = require('url');
        return url.resolve(this.basename, this.path);
    }
}

/** Apply the route basename to a named route in routes/Routes
 *  An invalid, non-existent named route gens a path for '/'
 **/
export class FindRoutesURL extends RouteURL {
    constructor(find_route_name) {
        var path = '/';
        let route = Routes.filter(r => r.name === find_route_name);

        if (route.length !== 0) {
            path = route[0].path;
        }

        super(path);
    }
}

export default class AppRouter extends React.Component {
    render() {
        return (
            <div style={{height: '100%'}}>
                <BrowserRouter basename={find_base_name()}>
                    <Switch>
                        <Route exact path="/" component={HomeRoute} />
                        <Route exact path="/login/" component={LoginRoute} />
                        <Route component={NotFoundRoute} />
                    </Switch>
                </BrowserRouter>
            </div>
        );
    }
}
