import React from 'react';
import { AboutMe, Posts } from '../components';
import './routes.css';

export class HomeRoute extends React.Component {
    render() {
        return (
            <div className="RoutePage">
              <div className="Layout-HR">
                <Posts />
                <AboutMe />
                <div style={{gridColumn: 2, gridRow: 2}}>
                    <p>Months and stuff</p>
                </div>
              </div>
            </div>
        );
    }
}
