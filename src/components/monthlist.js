import React from 'react';

export class MonthList extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        //TODO: Replace with query to backend
        return;
    }

    render() {
        return (
            <div style={{gridColumn: this.props.col, gridRow: this.props.row}}>
                <p>Months and stuff</p>
            </div>
        );
    }
}
MonthList.defaultProps = {
    col: "2",
    row: "2"
}
