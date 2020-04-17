import React from 'react';
import parse from 'html-react-parser';
import { PostDate } from './postdate';
import { FindRoutesURL } from '../router';
import './post.css';

/* Honest to God...
 * This is THE very first snippet I've ever copied from StackOverflow,|
 *                                                                   \/
 */
function isHTML (str) {
    var doc = new DOMParser().parseFromString(str, 'text/html');
    return Array.from(doc.body.childNodes).some(node => node.nodeType === 1);
}

export class Post extends React.Component {
    render () {
        const fragment = (contents) => {
            if (!isHTML(contents)) {
                return contents;
            }
            return parse(contents);
        };

        const footer_hide = (default_class) => {
            if (this.props.original === true) {
                if (default_class === '') {
                    return 'HidePostFooter';
                }

                const classes = ['HidePostFooter', default_class];
                return classes.join(' ');
            }
            return default_class;
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
                <hr className={footer_hide('')} />
                <div className={footer_hide('PostFooter')}>
                    <a href={`${(() => new FindRoutesURL('Post').get())()}${this.props.postID}`}>View Original Post</a>
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
    original: false
}
