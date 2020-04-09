import React from 'react';
import { FindRoutesURL } from '../router';
import { MONTHS } from '../util';

export class MonthListLink extends React.Component {
    render() {
        const linkCaption = `${MONTHS[this.props.month-1]}, ${this.props.year}`;

        return (
            <a href={`${(() => new FindRoutesURL('Posts').get())()}${this.props.year}/${this.props.month}`}>
                {linkCaption}
            </a>
        );
    }
}
MonthListLink.defaultProps = {
    month: 1,
    year: 2020,
};
