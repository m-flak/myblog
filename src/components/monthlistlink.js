import React from 'react';
import { FindRoutesURL } from '../router';

const MONTHS = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December',
];

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
