import React from 'react';
import parse from 'html-react-parser';
import { PostDate } from './postdate';
import './post.css';

/* Honest to God...
 * This is THE very first snippet I've ever copied from StackOverflow,|
 *                                                                   \/
 */
function isHTML(str) {
    var doc = new DOMParser().parseFromString(str, "text/html");
    return Array.from(doc.body.childNodes).some(node => node.nodeType === 1);
}

export class Post extends React.Component {
    render() {
        let fragment = (contents) => {
            if (!isHTML(contents)) {
                return contents;
            }
            return parse(contents);
        };

        return (
            <div className="Post">
                <div className="PostHeader">
                    <h4>{this.props.title}</h4>
                    <span className="PostPostedDate">
                        <PostDate date={this.props.datePosted} />
                    </span>
                </div>
                <hr/>
                <React.Fragment>
                    {fragment(this.props.contents)}
                </React.Fragment>
                <hr/>
                <div className="PostFooter">
                    <u>View Original Post</u>
                </div>
            </div>
        );
    }
}
Post.defaultProps = {
    postID: 0,
    posterID: 0,
    title: '',
    datePosted: '',
    contents: '',
}
