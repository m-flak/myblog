import React from 'react';
import PropTypes from 'prop-types';
import equal from 'fast-deep-equal';

export class FetchingComponentBase extends React.Component {
    doFetch () {

    }

    componentDidMount () {
        this.doFetch();
    }
}

export class FetchingComponent extends FetchingComponentBase {
    constructor (props) {
        super(props);

        this.state = {};
    }

    setFetchedStateVarLength (theLength) {
        return theLength;
    }

    shouldCallUpdateFetch () {
        return false;
    }

    componentDidUpdate (prevProps, prevState) {
        const prevStateFetched = prevState[this.props.fetchedStateVariable];
        const currStateFetched = this.state[this.props.fetchedStateVariable];

        if (prevProps.update !== this.props.update) {
            if (equal(prevState, this.state)) {
                this.doFetch();
                return;
            }
        }

        // My server likes to return blank arrays `\.('_')./'
        if (prevStateFetched.length > currStateFetched.length) {
            this.doFetch();
            return;
        }

        // If arcane stuff like in Posts must be done, allow us to.
        var numWhatever = this.setFetchedStateVarLength(currStateFetched.length);

        if (this.props.onUpdateFetch !== undefined && this.props.onUpdateFetch !== null) {
            // More arcane stuff, like if there are posts but we haven't got them.
            if (numWhatever > 0 || this.shouldCallUpdateFetch()) {
                const onUpdateFetch = this.props.onUpdateFetch;
                onUpdateFetch(numWhatever);
            }
        }
    }
}
FetchingComponent.defaultProps = {
    update: 0,
    fetchedStateVariable: 'component'
}
FetchingComponent.propTypes = {
    update: PropTypes.number,
    fetchedStateVariable: PropTypes.string,
    onUpdateFetch: PropTypes.func
}
