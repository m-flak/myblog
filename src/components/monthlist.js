import React from 'react';
import * as datetime from 'node-datetime';
import { getFromBackend } from '../util';
import { MonthListLink } from './monthlistlink';
import './monthlist.css';

/* Refer to: org.m_flak.myblog.server.routes.PostsRoute
 */
 const MODE_SUMMARY = 2;

export class MonthList extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            monthYears: [],
        };
    }

    componentDidMount() {
        getFromBackend('/posts', {mode: MODE_SUMMARY}, (data) => {
            if (data instanceof Array) {
                // If we got an array with single blank object, then do nothing
                if (data.length === 1 && data[0].hasOwnProperty('postID') === false) {
                    return;
                }

                // Get an array of {month, year}'s
                var monthYears = data.map((element) => {
                    const datePosted = datetime.create(element.datePosted);

                    return {
                        month: Number(datePosted.format('m')),
                        year: Number(datePosted.format('Y'))
                    };
                });

                // Remove duplicates
                var foundMonths = [];
                monthYears = monthYears.filter((element) => {
                    const condition = foundMonths.includes(element.month);

                    if (!condition) {
                        foundMonths.push(element.month);
                        return true;
                    }
                    return false;
                });

                // Acquire months & years so we can sort
                var allMonths = [];
                var allYears = [];
                monthYears.forEach((element) => {
                    allMonths.push(element.month);
                    allYears.push(element.year);
                });

                // Sort in Descending Order.
                const maxMonth = Math.max(...allMonths);
                const maxYear = Math.max(...allYears);
                monthYears.sort((a, b) => {
                    const distMonthA = maxMonth - a.month;
                    const distMonthB = maxMonth - b.month;
                    const distYearA = maxYear - a.year;
                    const distYearB = maxYear - b.year;
                    const distMinMonth = Math.min(distMonthA, distMonthB);
                    const distMinYear = Math.min(distYearA, distYearB);

                    if (distMinMonth === distMonthA && distMinYear === distYearA) {
                        return -1;
                    }
                    if (distMinMonth === distMonthB && distMinYear === distYearB) {
                        return 1;
                    }
                    return 0;
                });

                this.setState({monthYears: monthYears});
            }
        })
        .catch((error) => {
            return;
        });
    }

    render() {
        if (this.state.monthYears.length === 0) {
            return (
                <div style={{gridColumn: this.props.col, gridRow: this.props.row}}>
                    <h5>All Posts</h5>
                    <hr />
                    <p className="NothingToList">
                        Nothing's here...
                    </p>
                </div>
            );
        }

        return (
            <div style={{gridColumn: this.props.col, gridRow: this.props.row}}>
                <h5>All Posts</h5>
                <hr />
                <div className="MonthList">
                  <ul>
                    {this.state.monthYears.map((item, index) => {
                        return (
                            <li key={index}>
                                <MonthListLink month={item.month} year={item.year} />
                            </li>
                        );
                    })}
                  </ul>
                </div>
            </div>
        );
    }
}
MonthList.defaultProps = {
    col: 2,
    row: 2,
}
