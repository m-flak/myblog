import React from 'react';
import PropTypes from 'prop-types';
import { Post } from './post';
import { FetchingComponent } from './fetchingcomponent';
import { getFromBackend } from '../util';

/* Refer to: org.m_flak.myblog.server.routes.PostsRoute
 */
const MODE_FULL = 1;

const ERROR_POST_TITLE = 'We couldn\'t fetch any posts.';

/* **** FUTURE FIXME: When React no longer allows what's being done here....
 ***************************** `\.('_')./' **********************************/
function buildErrorHTML (errorMsg) {
    const errorHTML = [
        `<p>${errorMsg}</p>`,
        '<br/>',
        '<p>',
        'If you believe this to be in error, you may try: ',
        '<a href="javascript:window.location.reload();">reloading the page</a>.',
        '</p>'
    ];

    return errorHTML.join('');
}

export class Posts extends FetchingComponent {
    constructor (props) {
        super(props);

        this.state = {
            posts: []
        };
    }

    fetchPosts () {
        var args = { mode: MODE_FULL };

        if (Object.entries(this.props.filter).length > 0) {
            // The backend expects m & y as the args.
            Object.assign(args, { m: this.props.filter.month, y: this.props.filter.year });
        }

        getFromBackend('/posts', args, (data) => {
            try {
                if (data instanceof Array) {
                    if (data.length === 0) {
                        throw new Error('There are no posts yet for this blog.');
                    }
                    /* The server will return an array containing a blank object
                     *  if there are no posts.
                     */
                    else if (data.length === 1 && data[0].hasOwnProperty('postID') === false) {
                        throw new Error('There are no posts yet for this blog.');
                    }
                    this.setState({ posts: data });
                }
                else {
                    throw new Error('Backend returned malformed list.');
                }
            }
            catch (e) {
                this.setState({
                    posts: [
                        {
                            title: ERROR_POST_TITLE, contents: buildErrorHTML(e.message)
                        }
                    ]
                });
            }
        })
        .catch((error) => {
            this.setState({
                posts: [
                    {
                        title: ERROR_POST_TITLE, contents: buildErrorHTML(error.message)
                    }
                ]
            });
        });
    }

    // FROM: FetchingComponent
    doFetch () {
        this.fetchPosts();
    }

    // FROM: FetchingComponent
    setFetchedStateVarLength (theLength) {
        var numPosts = theLength;

        if (numPosts === 1) {
            try {
                const cond = this.state.posts[0].title.startsWith(ERROR_POST_TITLE);
                numPosts = cond ? 0 * numPosts : numPosts;
            }
            catch (e) {
                numPosts = 0;
            }
        }

        return numPosts;
    }

    shouldCallUpdateFetch () {
        var serversPostCount = window.sessionStorage.getItem('serverPostCount');

        if (serversPostCount !== null && serversPostCount > 0) {
            return true;
        }
        else if (serversPostCount !== null && serversPostCount === 0) {
            return false;
        }

        const pcPromise = new Promise((resolve, reject) => {
            getFromBackend('/postcount', {}, (data) => {
                resolve(Number(data));
            })
            .catch((error) => {
                resolve(0);
            });
        });

        return pcPromise.then((value) => {
            window.sessionStorage.setItem('serverPostCount', value);
            return value > 0 ? true : false;
        });
    }

    componentDidUpdate (prevProps, prevState) {
        super.componentDidUpdate(prevProps, prevState);
    }

    // A Spinner is our only child ;_~
    displayChildrenOrNot () {
        if (this.state.posts.length === 0) {
            return this.props.children;
        }

        return null;
    }

    render () {
        return (
            <>
            {this.displayChildrenOrNot()}
            {this.state.posts.map((post, index) => {
                return (
                    <div key={index} style={{ gridColumn: this.props.col, gridRow: this.props.row + index }}>
                        <Post postID={post.postID} posterID={post.posterID} title={post.title} datePosted={post.datePosted} contents={post.contents} />
                    </div>
                );
            })}
            </>
        );
    }
}
Posts.defaultProps = {
    filter: {},
    update: 0,
    fetchedStateVariable: 'posts', // Override from FetchingComponent
    col: 1,
    row: 1
}
Posts.propTypes = {
    filter: PropTypes.exact({
        month: PropTypes.number,
        year: PropTypes.number
    }),
    col: PropTypes.number,
    row: PropTypes.number
}
