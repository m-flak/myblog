import { isFullURL, backendURL } from './backend.js';

describe('isFullURL() method', () => {
    const isFullHTTPS = (addr) => isFullURL(addr.replace('http', 'https'));
    const isFullNoTrail = (addr) => isFullURL(addr.slice(0, addr.length - 1));

    it('knows what a full url is with a port', () => {
        const satansBlog = 'http://6.0.6.6:666/';
        expect(isFullURL(satansBlog)).toEqual(true);
        expect(isFullHTTPS(satansBlog)).toEqual(true);
        expect(isFullNoTrail(satansBlog)).toEqual(true);

    });

    it('knows what a full url is without a port', () => {
        const stevesBlog = 'http://developers.clipsnation.com/'
        expect(isFullURL(stevesBlog)).toEqual(true);
        expect(isFullHTTPS(stevesBlog)).toEqual(true);
        expect(isFullNoTrail(stevesBlog)).toEqual(true);
    });

    it('knows that a fully spec\'d url w/ a path is a full url', () => {
        const withAPath = 'http://evil.ru/hackerboogeyman/';
        expect(isFullURL(withAPath)).toEqual(true);
        expect(isFullHTTPS(withAPath)).toEqual(true);
        expect(isFullNoTrail(withAPath)).toEqual(true);
    });

    it('knows that a relative path is not a full URL', () => {
        const relPath = '/relative/';
        expect(isFullURL(relPath)).toEqual(false);
        expect(isFullNoTrail(relPath)).toEqual(false);
    });
});

describe('backendURL() method with http://ip:port/', () => {
    beforeEach(() => {
        process.env = Object.assign(process.env, { REACT_APP_BACKEND_URL: 'http://175.75.8.1:9000/' });
    });

    it('creates proper backend url', () => {
        const expected = 'http://175.75.8.1:9000/testingtest';
        expect(backendURL('/testingtest')).toEqual(expected);

        const oldRABU = process.env.REACT_APP_BACKEND_URL;
        process.env.REACT_APP_BACKEND_URL = oldRABU.slice(0, oldRABU.length - 1);
        expect(backendURL('/testingtest')).toEqual(expected);
    });
});

describe('backendURL() method with http://ip:port/subdir/', () => {
    beforeEach(() => {
        process.env = Object.assign(process.env, { REACT_APP_BACKEND_URL: 'http://175.75.8.1:9000/subdir/' });
    });

    it('creates proper backend url', () => {
        const expected = 'http://175.75.8.1:9000/subdir/testingtest';
        expect(backendURL('/testingtest')).toEqual(expected);

        const oldRABU = process.env.REACT_APP_BACKEND_URL;
        process.env.REACT_APP_BACKEND_URL = oldRABU.slice(0, oldRABU.length - 1);
        expect(backendURL('/testingtest')).toEqual(expected);
    });
});

describe('backendURL() method with http://ip/', () => {
    beforeEach(() => {
        process.env = Object.assign(process.env, { REACT_APP_BACKEND_URL: 'http://175.75.8.1/' });
    });

    it('creates proper backend url', () => {
        const expected = 'http://175.75.8.1/testingtest';
        expect(backendURL('/testingtest')).toEqual(expected);

        const oldRABU = process.env.REACT_APP_BACKEND_URL;
        process.env.REACT_APP_BACKEND_URL = oldRABU.slice(0, oldRABU.length - 1);
        expect(backendURL('/testingtest')).toEqual(expected);
    });
});

describe('backendURL() method with http://ip/subdir/', () => {
    beforeEach(() => {
        process.env = Object.assign(process.env, { REACT_APP_BACKEND_URL: 'http://175.75.8.1/subdir/' });
    });

    it('creates proper backend url', () => {
        const expected = 'http://175.75.8.1/subdir/testingtest';
        expect(backendURL('/testingtest')).toEqual(expected);

        const oldRABU = process.env.REACT_APP_BACKEND_URL;
        process.env.REACT_APP_BACKEND_URL = oldRABU.slice(0, oldRABU.length - 1);
        expect(backendURL('/testingtest')).toEqual(expected);
    });
});

describe('backendURL() method with http://domain/', () => {
    beforeEach(() => {
        process.env = Object.assign(process.env, { REACT_APP_BACKEND_URL: 'http://example.com/' });
    });

    it('creates proper backend url', () => {
        const expected = 'http://example.com/testingtest';
        expect(backendURL('/testingtest')).toEqual(expected);

        const oldRABU = process.env.REACT_APP_BACKEND_URL;
        process.env.REACT_APP_BACKEND_URL = oldRABU.slice(0, oldRABU.length - 1);
        expect(backendURL('/testingtest')).toEqual(expected);
    });
});

describe('backendURL() method with http://domain/subdir/', () => {
    beforeEach(() => {
        process.env = Object.assign(process.env, { REACT_APP_BACKEND_URL: 'http://example.com/subdir/' });
    });

    it('creates proper backend url', () => {
        const expected = 'http://example.com/subdir/testingtest';
        expect(backendURL('/testingtest')).toEqual(expected);

        const oldRABU = process.env.REACT_APP_BACKEND_URL;
        process.env.REACT_APP_BACKEND_URL = oldRABU.slice(0, oldRABU.length - 1);
        expect(backendURL('/testingtest')).toEqual(expected);
    });
});

describe('backendURL() method with /subdir/', () => {
    beforeEach(() => {
        process.env = Object.assign(process.env, { REACT_APP_BACKEND_URL: '/subdir/' });
    });

    it('creates proper backend url', () => {
        const expected = 'http://localhost/subdir/testingtest';
        expect(backendURL('/testingtest')).toEqual(expected);

        const oldRABU = process.env.REACT_APP_BACKEND_URL;
        process.env.REACT_APP_BACKEND_URL = oldRABU.slice(0, oldRABU.length - 1);
        expect(backendURL('/testingtest')).toEqual(expected);
    });
});
