import React from 'react';
import { Post } from './post';
import { getFromBackend } from '../util';

/* Refer to: org.m_flak.myblog.server.routes.PostsRoute
 */
const MODE_FULL = 1;

export class Posts extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            posts: [],
        };
    }

    fetchPosts() {
        var args = {mode: MODE_FULL};

        if (Object.entries(this.props.filter).length > 0) {
            // The backend expects m & y as the args.
            Object.assign(args, {m: this.props.filter.month, y: this.props.filter.year});
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
                    this.setState({posts: data});
                }
                else {
                    throw new Error('Backend returned malformed list.');
                }
            }
            catch (e) {
                this.setState({
                    posts: [
                        {
                            title: 'We couldn\'t fetch any posts.', contents: e.message
                        },
                    ],
                });
            }
        })
        .catch((error) => {
            this.setState({
                posts: [
                    {
                        title: 'We couldn\'t fetch any posts.', contents: error.message
                    },
                ],
            });
        });
    }

    componentDidMount() {
        this.fetchPosts();
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevProps.update !== this.props.update) {
            this.fetchPosts();
        }

        // My server likes to return blank arrays `\.('_')./'
        if (prevState.posts.length > this.state.posts.length) {
            this.fetchPosts();
        }
    }

    render() {
        return (
            <>
            {this.state.posts.map((post, index) => {
                return (
                    <div key={index} style={{gridColumn: this.props.col, gridRow: this.props.row+index}}>
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
    col: 1,
    row: 1,
}
