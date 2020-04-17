import React from 'react';
import PropTypes from 'prop-types';
import { Button } from 'reactstrap'
import { MONTHS } from '../util';
import { FindRoutesURL } from '../router';
import './filterheader.css';

export class FilterHeader extends React.Component {
    render () {
        const backHref = new FindRoutesURL('Home').get();

        return (
            <div style={{ gridColumn: this.props.col, gridRow: this.props.row }}>
                <div className="FilterHeader">
                    <Button tag="a" color="primary" href={backHref}>&#x1418; Return</Button>
                    <h3>Showing all posts from: {MONTHS[this.props.month - 1]}, {this.props.year}:</h3>
                </div>
            </div>
        );
    }
}
FilterHeader.defaultProps = {
    month: 1,
    year: 2020,
    row: 1,
    col: 1
}
FilterHeader.propTypes = {
    month: PropTypes.number,
    year: PropTypes.number,
    row: PropTypes.number,
    col: PropTypes.number
}
