import React from 'react';
import {Session} from 'bc-react-session';
import { AboutMe, Posts, MonthList, NewPost } from '../components';
import './routes.css';

export class HomeRoute extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            canPost: false,
            rowOffset: 0,
        };
    }

    componentDidMount() {
        let session = Session.get();
        if (session.isValid) {
            this.setState({canPost: true, rowOffset: 1});
        }
        else {
            this.setState({canPost: false, rowOffset: 0});
        }
    }

    render() {
        return (
            <div className="RoutePage">
              <div className="Layout-HR">
                <NewPost canPost={this.state.canPost} />
                <Posts row={this.state.rowOffset+1} />
                <AboutMe row={this.state.rowOffset+1} />
                <MonthList row={this.state.rowOffset+2} />
              </div>
            </div>
        );
    }
}
