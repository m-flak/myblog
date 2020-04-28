import { Routes } from './routes';
import { RouteURL } from './router.js';

describe('RouteURL with root basename', () => {
    beforeEach(() => {
        process.env = Object.assign(process.env, { REACT_APP_BASENAME: '/' });
    });

    it('applies the root basename correctly to all routes', () => {
        Routes.forEach((rt) => {
            const withBasename = new RouteURL(rt.path);
            expect(withBasename.get()).toEqual(rt.path);
        });
    });
});

describe('RouteURL with /blog/ basename', () => {
    beforeEach(() => {
        process.env = Object.assign(process.env, { REACT_APP_BASENAME: '/blog/' });
    });

    it('applies the /blog/ basename correctly to all routes', () => {
        const expectedRoutes = [
            '/blog/',
            '/blog/post/',
            '/blog/posts/',
            '/blog/login/',
            '/blog/logout/'
        ];

        Routes.forEach((rt, i) => {
            const withBasename = new RouteURL(rt.path);
            expect(withBasename.get()).toEqual(expectedRoutes[i]);
        });
    });
});
