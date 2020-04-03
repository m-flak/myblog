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

    componentDidMount() {
        //TODO: Replace with query to backend
        getFromBackend('/posts', {mode: MODE_FULL}, (data) => {
            try {
                if (data instanceof Array) {
                    if (data.length === 0) {
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
        /*
        this.setState({
            posts: [
                {
                    title: 'A Post', contents: 'What it do'
                },
                {
                    title: 'ANOTHER! Post', contents: 'SUP!'
                },
            ],
        });
        */
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
    col: "1",
    row: "1"
}
