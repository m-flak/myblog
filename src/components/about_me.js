import React from 'react';
import { FetchingComponentBase } from './fetchingcomponent';
import { getFromBackend } from '../util';

export class AboutMe extends FetchingComponentBase {
    constructor (props) {
        super(props);

        this.state = {
            aboutMe: ''
        };
    }

    doFetch () {
        getFromBackend('/about', {}, (data) => {
            this.setState({aboutMe: data});
        })
        .catch((error) => {
            this.setState({aboutMe: error.message});
        });
    }

    render () {
        return (
            <div style={{ gridColumn: this.props.col, gridRow: this.props.row }}>
                <h5>About Me</h5>
                <hr/>
                <p>{this.state.aboutMe}</p>
            </div>
        );
    }
}
AboutMe.defaultProps = {
    col: 2,
    row: 1
}
