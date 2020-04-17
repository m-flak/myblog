import React from 'react';
import ReactQuill from 'react-quill';
import { Alert, Button, InputGroup, InputGroupAddon, InputGroupText, Input } from 'reactstrap';
import { Session } from 'bc-react-session';
import { postToBackend } from '../util';
import 'react-quill/dist/quill.snow.css';
import './newpost.css';

const HIDE = { display: 'none' };
const TITLE_PLACEHOLDER = 'Untitled Post';

const IS_FOCUSED = (yes_no) => {
    return yes_no;
};

export class NewPost extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            isEmpty: true,
            newPostContents: '',
            newPostTitle: TITLE_PLACEHOLDER,
            succeeded: false,
            failed: false
        };

        this.titleEditFocused = false;

        this.handleChange = this.handleChange.bind(this);
        this.handleTitleChange = this.handleTitleChange.bind(this);
        this.handleTitleFocused = this.handleTitleFocused.bind(this);
        this.handleTitleUnfocused = this.handleTitleUnfocused.bind(this);
        this.composePostClick = this.composePostClick.bind(this);
    }

    shouldComponentUpdate () {
        /* Using onChange event on a text input is very expensive!
         * WHEW! This'll prevent the lag tho!!
         */
        if (IS_FOCUSED(this.titleEditFocused)) {
            return false;
        }

        return true;
    }

    handleChange (value) {
        const isEmpty = !(value.length > 0);

        this.setState({ newPostContents: value, isEmpty: isEmpty });
    }

    handleTitleChange (event) {
        const placeholder = event.target.placeholder;
        const newTitle = event.target.value;

        if (newTitle.length < 1) {
            this.setState({ newPostTitle: placeholder });
            return;
        }

        this.setState({ newPostTitle: newTitle });
    }

    handleTitleFocused (event) {
        this.titleEditFocused = true;
    }

    handleTitleUnfocused (event) {
        this.titleEditFocused = false;
    }

    composePostClick (event) {
        const payload = Session.getPayload();
        var wasSuccessful = true;

        postToBackend('/poststory', { token: payload.accessToken, title: this.state.newPostTitle, contents: this.state.newPostContents }, (data) => {
            this.setState({ succeeded: true });
            wasSuccessful = true;
        })
        .catch((error) => {
            console.log(error.stack);
            this.setState({ failed: true });
            wasSuccessful = false;
        });

        if (this.props.onCompose !== undefined && this.props.onCompose !== null) {
            const onCompose = this.props.onCompose;
            onCompose(wasSuccessful);
        }
    }

    render () {
        const display_alert = (state) => {
            return state ? 'AlertShow' : 'AlertHide';
        };

        if (!this.props.canPost) {
            return (
                <div style={HIDE}></div>
            );
        }

        return (
            <div className="NewPost" style={{ gridColumn: this.props.col, gridRow: this.props.row, gridColumnEnd: 'span 2' }}>
                <h3>Compose a New Post:</h3>
                <hr />
                <div className="NewPostAlertHolder">
                    <Alert className={display_alert(this.state.succeeded)} color="success">Posting Successful!</Alert>
                    <Alert className={display_alert(this.state.failed)} color="danger">Posting Failed!</Alert>
                </div>
                <div className="NewPostTitleHolder">
                    <InputGroup>
                        <InputGroupAddon addonType="prepend">
                            <InputGroupText>New Post Title:</InputGroupText>
                        </InputGroupAddon>
                        <Input placeholder={TITLE_PLACEHOLDER} onFocus={this.handleTitleFocused} onBlur={this.handleTitleUnfocused} onChange={this.handleTitleChange} />
                    </InputGroup>
                </div>
                <ReactQuill value={this.state.newPostContents} onChange={this.handleChange} />
                <div className="NewPostButtonHolder">
                    <Button color="primary" size="lg" disabled={this.state.isEmpty} onClick={this.composePostClick} block>Compose Post</Button>
                </div>
            </div>
        );
    }
}
NewPost.defaultProps = {
    col: 1,
    row: 1,
    canPost: false
}
