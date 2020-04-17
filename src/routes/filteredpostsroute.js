import React from 'react';
import { Posts, FilterHeader } from '../components';
import './routes.css';

export class FilteredPostsRoute extends React.Component {
    render () {
        const { match: { params } } = this.props;

        return (
            <div className="RoutePage">
              <div className="Layout-FPR">
                <FilterHeader month={params.month} year={params.year} />
                <Posts row={2} filter={{ month: params.month, year: params.year }} />
              </div>
            </div>
        );
    }
}
