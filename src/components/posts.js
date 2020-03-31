import React from 'react';
import { Post } from './post';

export class Posts extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            posts: [],
        };
    }

    componentDidMount() {
        //TODO: Replace with query to backend
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
    }

    render() {
        return (
            <>
            {this.state.posts.map((post, index) => {
                return (
                    <div key={index} style={{gridColumn: this.props.col, gridRow: this.props.row+index}}>
                        <Post title={post.title} contents={post.contents} />
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
