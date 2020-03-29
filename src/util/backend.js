import * as axios from 'axios';
import * as pathbrowser from 'path-browserify';
import * as q_string from 'query-string';
import * as url from 'url';

/*
 * Ensure that a response is in the proper format (JSON)
 */
function formatResponse(response) {
    var responseData;

    // Determine if our response was properly sent as json.
    Object.keys(response.headers).forEach((headerName) => {
        if (responseData !== undefined) {
            return;
        }

        // Axios lowercases all headers for some reason...
        if (headerName === 'content-type') {
            if (response.headers[headerName].includes('json')) {
                responseData = response.data;
            }
        }
    });
    // It wasn't-- Parse it!
    if (responseData === undefined) {
        responseData = JSON.parse(response.data);
    }

    return responseData;
}

/*
 * Simply checks if a URL is a full URL.
 */
function isFullURL(an_url) {
    var urlObject = url.parse(an_url, false, false);
    // A 'full' URL for the backend is http://blah:8188/
    var neededParts = ['protocol', 'host', 'hostname', 'port'];
    var isFull = true;

    for (const i in neededParts) {
        let key = neededParts[i];
        if (urlObject[key] === '' || urlObject[key] === undefined) {
            isFull = false;
            break;
        }
    }

    return isFull;
}

/*
 * Creates a backend url from a requested resource (e.g.: '/request')
 */
function backendURL(requested_resource) {
    var dotenvURL = process.env.REACT_APP_BACKEND_URL;

    // dotenvURL is a full url.
    if (isFullURL(dotenvURL)) {
        return url.resolve(dotenvURL, requested_resource);
    }

    // dotenvURL is a relative path
    var serverURLObj = url.parse(window.location.href, false, false);
    serverURLObj['pathname'] = '';

    // Create our backend query base url
    var backendURL = url.resolve(url.format(serverURLObj), dotenvURL);

    return pathbrowser.join(backendURL, requested_resource);
}

/*
 * This shall execute a GET request at the following backend route.
 *
 * api_url -> a string equalling a route (e.g.: '/posts', '/request')
 * dict_args -> an optional dictionary object containing parameters for the URL
 * callback -> callback(response_data)
 */
export function getFromBackend(api_url, dict_args, callback) {
    var queryURL = backendURL(api_url);

    // Check for parameters to add as a query string
    if (dict_args !== null && dict_args instanceof Object) {
        // Make sure it is not empty...
        if (Object.entries(dict_args).length > 0) {
            var queryString = q_string.stringify(dict_args, {});

            queryURL = queryURL+'?'+queryString;
        }
    }

    // Execute the GET request!!!
    return axios.get(queryURL)
        .then((response) => {
            var responseData = formatResponse(response);

            if (responseData.errorCode === 'FAIL') {
                throw new Error('errorCode: FAIL');
            }

            return callback(responseData.data);
        });
}

/*
 * This shall send a javascript object as JSON in a POST request to the following backend route.
 *
 * api_url ->  a string equalling a route (e.g.: '/posts', '/request')
 * post_what -> a javascript object containing information to send
 * callback -> callback(response_data)
 */
export function postToBackend(api_url, post_what, callback) {
    var postURL = backendURL(api_url);

    return axios.request({
            url: postURL,
            method: 'post',
            transformRequest: [(data, headers) => {
                // This is required so that Axios will send data as JSON.
                headers['Content-Type'] = 'application/json';
                return data;
            }],
            data: JSON.stringify(post_what)
        })
        .then((response) => {
            var responseData = formatResponse(response);

            if (responseData.errorCode === 'FAIL') {
                throw new Error('errorCode: FAIL');
            }

            return callback(responseData.data);
        });
}
