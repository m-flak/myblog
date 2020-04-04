import React from 'react';
import { Card, CardBody } from 'reactstrap';
import { Post } from '../components';
import { getFromBackend } from '../util';
import './routes.css';

export class PostRoute extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            post: {},
        };
    }

    componentDidMount() {
        const {match: {params}} = this.props;
        const errorPost = {
            postID: -1,
            posterID: -1,
            title: 'This Post Doesn\'t Exist.',
            datePosted: '',
            contents: 'Please check the URL and try again.',
        };

        getFromBackend('/viewpost', {id: params.postID}, (data) => {
            if (data.hasOwnProperty('postID') === true) {
                this.setState({post: data});
            }
            else {
                this.setState({post: errorPost});
            }
        })
        .catch((error) => {
            var errorMsgPost = errorPost;
            errorMsgPost.contents = "ERROR: "+error.message;
            this.setState({post: errorMsgPost});
        });
    }

    render() {
        return (
            <div className="RoutePage">
              <div className="Layout-PR">
                <Card color="dark">
                    <CardBody>
                        <Post original={true}
                              postID={this.state.post.postID}
                              posterID={this.state.post.posterID}
                              title={this.state.post.title}
                              datePosted={this.state.post.datePosted}
                              contents={this.state.post.contents} />
                    </CardBody>
                </Card>
              </div>
            </div>
        );
    }
}
