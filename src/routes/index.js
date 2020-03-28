export { HomeRoute } from './homeroute';
export { NotFoundRoute } from './notfoundroute';
export { LoginRoute } from './loginroute.js';

export const Routes = [
    {name: 'Home', path: '/', navTo: true},
    {name: 'Posts', path: '/posts/', navTo: false},
    {name: 'Login', path: '/login/', navTo: true},
    {name: 'Logout', path: '/logout/', navTo: false},
];
