import React from 'react';
import equal from 'fast-deep-equal';

export class FetchingComponent extends React.Component {
    constructor(props) {
        super(props);

        this.state = {};
    }

    doFetch() {
        return;
    }

    setFetchedStateVarLength(theLength) {
        return theLength;
    }

    componentDidMount() {
        this.doFetch();
    }

    componentDidUpdate(prevProps, prevState) {
        let prevStateFetched = prevState[this.props.fetchedStateVariable];
        let currStateFetched = this.state[this.props.fetchedStateVariable];

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
            if (numWhatever > 0) {
                let onUpdateFetch = this.props.onUpdateFetch;
                onUpdateFetch(numWhatever);
            }
        }
    }
}
FetchingComponent.defaultProps = {
    update: 0,
    fetchedStateVariable: 'component',
}
