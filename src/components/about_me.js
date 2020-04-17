import React from 'react';

export class AboutMe extends React.Component {
    constructor (props) {
        super(props);

        this.state = {
            aboutMe: ''
        };
    }

    componentDidMount () {
        // TODO: Replace with query to backend
        this.setState({
            aboutMe: 'Don\'t forget to replace me with a query to the backend!'
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
