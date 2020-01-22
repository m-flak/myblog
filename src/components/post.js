import React from 'react';
import './post.css';

export class Post extends React.Component {
    render() {
        return (
            <div className="Post">
            <h4>{this.props.title}</h4>
            <hr/>
            <p>{this.props.contents}</p>
            </div>
        );
    }
}
